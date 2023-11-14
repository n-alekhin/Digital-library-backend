package com.springproject.core.model.Elastic;

import lombok.*;

import java.util.Map;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoolSearch {
    private Map<String, ElasticMathQuery> must;
    private Map<String, String> should;
    private Map<String, String> filter;
}
