package com.springproject.core.Services;

import com.springproject.core.Entity.Elastic.BoolSearch;
import com.springproject.core.Entity.Elastic.ElasticBook;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ElasticsearchOperations operations;

    @Override
    public List<ElasticBook> searchBookBool(BoolSearch query) {
        List<ElasticBook> books = new LinkedList<>();
        Query query1 = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b.must(
                        query.getMust().keySet().stream().map(key ->
                                co.elastic.clients.elasticsearch._types.query_dsl.Query
                                        .of(innerQ -> innerQ.match(m -> m.field(key)
                                                .query(query.getMust().get(key).getQuery())
                                                .operator(query.getMust().get(key).getOperator())
                                        ))
                        ).collect(Collectors.toList())
                ))).build();
        query1.setStoredFields(Arrays.asList("title", "id"));
        operations.search(query1, ElasticBook.class).getSearchHits().forEach(h -> books.add(h.getContent()));
        return books;
    }
}
