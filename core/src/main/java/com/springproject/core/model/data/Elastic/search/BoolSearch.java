package com.springproject.core.model.data.Elastic.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoolSearch {
    private Map<String, ElasticBoolQuery> must;
    @Schema(hidden = true)
    private Map<String, String> should;
    @Schema(hidden = true)
    private Map<String, String> filter;
}
