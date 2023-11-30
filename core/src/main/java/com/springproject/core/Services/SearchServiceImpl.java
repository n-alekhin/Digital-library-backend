package com.springproject.core.Services;

import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.dto.BookDTO;
import com.springproject.core.model.Constants;
import com.springproject.core.model.Elastic.search.BoolSearch;
import com.springproject.core.model.Elastic.ElasticBook;
import com.springproject.core.model.Elastic.search.KnnSearch;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ElasticsearchOperations operations;
    private final Constants constants;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private BoolQuery.Builder buildBoolQuery(BoolQuery.Builder b, BoolSearch query) {
        if (query.getMust() != null && !query.getMust().keySet().isEmpty()) {
            b.must(
                    query.getMust().keySet().stream().map(key ->
                            co.elastic.clients.elasticsearch._types.query_dsl.Query
                                    .of(innerQ -> innerQ.match(m -> m.field(key)
                                            .query(query.getMust().get(key).getQuery())
                                            .operator(query.getMust().get(key).getOperator())
                                            .fuzziness(query.getMust().get(key).getFuzzy())
                                    ))
                    ).collect(Collectors.toList())
            );
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

    @Override
    public List<BookDTO> searchBookBool(BoolSearch query) {
        Query query1 = new NativeQueryBuilder()
                .withPageable(PageRequest.of(0, 10))
                .withStoredFields(Collections.singletonList("id"))
                .withQuery(q -> q.bool(b -> buildBoolQuery(b, query))).build();
        List<Long> ids = operations.search(query1, ElasticBook.class).getSearchHits().stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }
    private KnnQuery buildKnnQuery(KnnSearch query) {
        return KnnQuery.of(q -> q
                .field(query.getField())
                .k(query.getK())
                .numCandidates(query.getNumCandidates())
                .queryVector(query.getQuery_vector()));
    }
    @Override
    public List<BookDTO> searchBookKnn(KnnSearch query) {
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(query)).build();
        List<Long> ids = operations.search(queryForElastic, ElasticBook.class).getSearchHits().stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }

    @Override
    public List<BookDTO> searchBookKnnAndBool(KnnSearch knnQuery, BoolSearch boolQuery) {
        Query queryForElastic = new NativeQueryBuilder()
                .withSearchType(null)
                .withStoredFields(Collections.singletonList("id"))
                .withKnnQuery(buildKnnQuery(knnQuery))
                .withQuery(q -> q.bool(b -> buildBoolQuery(b, boolQuery))).build();
        List<Long> ids = operations.search(queryForElastic, ElasticBook.class).getSearchHits().stream().map(h -> h.getContent().getId()).collect(Collectors.toList());
        return getBooksByIds(ids);
    }

    private List<BookDTO> getBooksByIds(List<Long> ids) {
        return bookRepository.findAllById(ids).stream().map(b -> {
            BookDTO book = modelMapper.map(b, BookDTO.class);
            book.setCoverImageUrl(constants.getImagePath() + b.getId());
            return book;
        }).collect(Collectors.toList());
    }
}
