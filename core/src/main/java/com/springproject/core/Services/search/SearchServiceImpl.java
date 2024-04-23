package com.springproject.core.Services.search;

import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.data.Constants;
import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.data.Elastic.search.ElasticBoolQuery;
import com.springproject.core.model.dto.KnnDTO;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import com.springproject.core.model.dto.WikidataSearchDTO;
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
    private final WikidataService wikidataService;

    private BoolQuery.Builder buildBoolQuery(BoolQuery.Builder b, BoolSearch query) {
        ElasticBoolQuery nested = query.getMust().get("chapters.content");
        List<co.elastic.clients.elasticsearch._types.query_dsl.Query> queries = new ArrayList<>();
        if (nested != null && !nested.getQuery().isEmpty()) {
            queries.add(co.elastic.clients.elasticsearch._types.query_dsl.Query
                    .of(q -> q.nested(n -> n
                            .path("chapters")
                            .query(qi -> qi
                                    .bool(bool -> bool
                                            .must(must -> must
                                                    .match(m -> m
                                                            .field("chapters.content")
                                                            .query(nested.getQuery())
                                                            .operator(nested.getOperator())
                                                    )
                                            )
                                    )

                            )
                    )));
            System.out.println(query.getMust().get("chapters.content"));
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
        if (query.getMust() != null && !queries.isEmpty()) {
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
        Query queryForElastic = new NativeQueryBuilder()
                .withPageable(PageRequest.of(0, 20))
                .withStoredFields(Collections.singletonList("id"))
                .withQuery(q -> q
                        .scriptScore(ssq -> ssq
                                .query(innerQ -> innerQ.bool(b -> buildBoolQuery(b, query)))
                                .script(s -> s.inline(inlineS -> inlineS.source("_score * (1 + 0.1 * (Math.log10(1 + doc['reviews'].value)))")))
                        )).build();
        return searchBooks(queryForElastic);
    }


    @Override
    public List<BookDTO> searchBookKnn(KnnDTO knnDTO) {
        KnnSearch query = new KnnSearch();
        query.setK(knnDTO.getK());
        query.setNumCandidates(knnDTO.getNumCandidates());
        query.setQuery_vector(vectorService.getVector(knnDTO.getQuery()));

        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withPageable(PageRequest.of(0, 20))
                .withStoredFields(Collections.singletonList("id"))
                .withQuery(q -> q
                        .scriptScore(ssq -> ssq
                                .query(innerQ -> innerQ.matchAll(ma -> ma))
                                .script(s -> s.inline(inlineS ->
                                        inlineS.source("0.02 * (Math.log10(1 + doc['reviews'].value))")
                                        )
                                )
                        ))
                .withKnnQuery(buildKnnQuery(query)).build();

        return searchBooks(queryForElastic);
    }
    private List<BookDTO> searchBooks(Query queryForElastic) {
        List<SearchHit<ElasticBook>> hits = operations.search(queryForElastic, ElasticBook.class).getSearchHits();
        hits.forEach(h -> log.info(h.getId() + " " + h.getScore()));
        List<Long> ids = hits.stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }
    @Override
    public List<SearchHit<ElasticBook>> searchBookHitsKnn(KnnSearch query) {
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(query)).build();
        return operations.search(queryForElastic, ElasticBook.class).getSearchHits();
    }

    @Override
    public List<BookDTO> searchBookKnnExpanded(KnnDTO knnDTO) {
        KnnSearch knnQuery = new KnnSearch();
        BoolSearch boolQuery = new BoolSearch();
        knnQuery.setK(knnDTO.getK());
        knnQuery.setNumCandidates(knnDTO.getNumCandidates());
        knnQuery.setQuery_vector(vectorService.getVector(knnDTO.getQuery()));
        System.out.println(knnDTO.getBoost());
        ElasticBoolQuery searchByTitle = new ElasticBoolQuery();
        searchByTitle.setQuery(String.join(" ", vectorService.getNounChunks(knnDTO.getQuery(), true)));
        boolQuery.setMust(Map.of("title", searchByTitle));
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withPageable(PageRequest.of(0, 20))
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(knnQuery, knnDTO.getBoost()))
                .withQuery(q -> q.bool(b -> buildBoolQuery(b, boolQuery))).build();

        return searchBooks(queryForElastic);
    }

    @Override
    public List<BookDTO> wikidataSearch(WikidataSearchDTO wikidataSearchDTO) {
        BoolSearch boolSearch =  new BoolSearch();
        ElasticBoolQuery searchByContent = new ElasticBoolQuery();
        searchByContent.setQuery(wikidataService.enrichWithWikidata(wikidataSearchDTO.getQuery()));
        Map<String, ElasticBoolQuery> map = new HashMap<>();
        map.put("chapters.content", searchByContent);
        boolSearch.setMust(map);
        System.out.println(boolSearch.getMust().get("chapters.content").getQuery());
        return searchBookBool(boolSearch);
    }

}
