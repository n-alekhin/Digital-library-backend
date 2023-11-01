package com.springproject.core.Entity.Elastic;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
@Getter
@Builder
public class BoolSearch {
    private Map<String, ElasticMathQuery> must;
    private Map<String, String> should;
    private Map<String, String> filter;
}
