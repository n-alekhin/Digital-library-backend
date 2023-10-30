package com.springproject.core.Entity.Elastic;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ElasticMathQuery {
    private String query;
    private Operator operator;
}
