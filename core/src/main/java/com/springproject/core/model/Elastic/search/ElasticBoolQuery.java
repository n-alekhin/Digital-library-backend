package com.springproject.core.model.Elastic.search;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ElasticBoolQuery {
    @Schema(example = "Harry Potter")
    private String query;
    @Schema(example = "And")
    private Operator operator = Operator.Or;
    @Schema(example = "AUTO:3,10")
    private String fuzzy = null;
}
