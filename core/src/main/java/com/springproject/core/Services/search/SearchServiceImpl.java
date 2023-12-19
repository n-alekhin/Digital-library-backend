package com.springproject.core.Services.search;

import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.data.Constants;
import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.data.Elastic.search.ElasticBoolQuery;
import com.springproject.core.model.data.Elastic.search.Knn;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ElasticsearchOperations operations;
    private final Constants constants;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private final VectorService vectorService;

    private BoolQuery.Builder buildBoolQuery(BoolQuery.Builder b, BoolSearch query) {
        ElasticBoolQuery nested = query.getMust().get("chapters.content");
        List<co.elastic.clients.elasticsearch._types.query_dsl.Query> queries = new ArrayList<>();
        if (nested != null && !nested.getQuery().isEmpty()) {
            queries.add(co.elastic.clients.elasticsearch._types.query_dsl.Query
                    .of(q -> q.nested(n -> n
                            .path("chapters")
                            .query(qi -> qi
                                    .match(m -> m
                                            .field("chapters.content")
                                            .query(nested.getQuery())
                                            .operator(nested.getOperator())
                                    )
                            )
                    )));
            query.getMust().remove("chapters.content");
        }
        queries.addAll(query.getMust().keySet().stream().filter(k ->
                !query.getMust().get(k).getQuery().isEmpty()).map(key ->
                co.elastic.clients.elasticsearch._types.query_dsl.Query
                        .of(innerQ -> innerQ.match(m -> m.field(key)
                                .query(query.getMust().get(key).getQuery())
                                .operator(query.getMust().get(key).getOperator())
                                .fuzziness(query.getMust().get(key).getFuzzy())
                        ))
        ).toList());
        if (query.getMust() != null && !query.getMust().keySet().isEmpty()) {
            b.must(queries);
        }
        if (query.getFilter() != null && !query.getFilter().keySet().isEmpty()) {
            b.filter(
                    query.getFilter().keySet().stream().map(key ->
                            co.elastic.clients.elasticsearch._types.query_dsl.Query
                                    .of(innerQ -> innerQ.term(t -> t
                                            .field(key)
                                            .value(query.getFilter().get(key))))
                    ).collect(Collectors.toList())
            );
        }
        return b;
    }

    private KnnQuery buildKnnQuery(KnnSearch query, float boost) {
        return KnnQuery.of(q -> q
                .boost(boost)
                .field(query.getField())
                .k(query.getK())
                .numCandidates(query.getNumCandidates())
                .queryVector(query.getQuery_vector()));
    }
    private KnnQuery buildKnnQuery(KnnSearch query) {
        return KnnQuery.of(q -> q
                .field(query.getField())
                .k(query.getK())
                .numCandidates(query.getNumCandidates())
                .queryVector(query.getQuery_vector()));
    }
    private List<BookDTO> getBooksByIds(List<Long> ids) {
        return bookRepository.findAllById(ids).stream().map(b -> {
            BookDTO book = modelMapper.map(b, BookDTO.class);
            book.setCoverImageUrl(constants.getImagePath() + b.getId());
            return book;
        }).sorted(Comparator.comparingInt(book -> ids.indexOf(book.getId())))
                .toList();
    }

    @Override
    public List<BookDTO> searchBookBool(BoolSearch query) {
        Query query1 = new NativeQueryBuilder()
                .withPageable(PageRequest.of(0, 10))
                .withStoredFields(Collections.singletonList("id"))
                .withQuery(q -> q.bool(b -> buildBoolQuery(b, query))).build();
        List<SearchHit<ElasticBook>> hits = operations.search(query1, ElasticBook.class).getSearchHits();
        hits.forEach(h -> log.info(h.getId() + " " + h.getScore()));
        List<Long> ids = hits.stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }


    @Override
    public List<BookDTO> searchBookKnn(Knn knn) {
        KnnSearch query = new KnnSearch();
        query.setK(knn.getK());
        query.setNumCandidates(knn.getNumCandidates());
        query.setQuery_vector(vectorService.getVector(knn.getQuery()));
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(query)).build();
        List<SearchHit<ElasticBook>> hits = operations.search(queryForElastic, ElasticBook.class).getSearchHits();
        hits.forEach(h -> log.info(h.getId() + " " + h.getScore()));
        List<Long> ids = hits.stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }

    @Override
    public List<BookDTO> searchBookKnnAndBool(Knn knn) {
        KnnSearch knnQuery = new KnnSearch();
        BoolSearch boolQuery = new BoolSearch();
        knnQuery.setK(knn.getK());
        knnQuery.setNumCandidates(knn.getNumCandidates());
        knnQuery.setQuery_vector(vectorService.getVector(knn.getQuery()));
        System.out.println(knn.getBoost());
        ElasticBoolQuery searchByTitle = new ElasticBoolQuery();
        searchByTitle.setQuery(String.join(" ", vectorService.getNounChunks(knn.getQuery(), true)));
        boolQuery.setMust(Map.of("title", searchByTitle));
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(knnQuery, knn.getBoost()))
                .withQuery(q -> q.bool(b -> buildBoolQuery(b, boolQuery))).build();
        List<SearchHit<ElasticBook>> hits = operations.search(queryForElastic, ElasticBook.class).getSearchHits();
        hits.forEach(h -> log.info(h.getId() + " " + h.getScore()));
        List<Long> ids = hits.stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }

}
