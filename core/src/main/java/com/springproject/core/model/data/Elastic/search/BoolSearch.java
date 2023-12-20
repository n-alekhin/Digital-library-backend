package com.springproject.core.model.data.Elastic.search;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoolSearch {
    public BoolSearch(){
        must = new HashMap<>();
        should = new HashMap<>();
        filter = new HashMap<>();
    }

    private Map<String, ElasticBoolQuery> must;
    @Schema(hidden = true)
    private Map<String, String> should;
    @Schema(hidden = true)
    private Map<String, String> filter;
    public void addShouldCondition(String key, String value) {
        this.should.put(key, value);
    }

    public void addMustCondition(String key, String value) {
        ElasticBoolQuery elasticBoolQuery = new ElasticBoolQuery();
        System.out.println(value);
        elasticBoolQuery.setQuery(value);
        elasticBoolQuery.setOperator(Operator.Or);
        System.out.println(elasticBoolQuery.getQuery());
        this.must.put(key, elasticBoolQuery);
    }
}
