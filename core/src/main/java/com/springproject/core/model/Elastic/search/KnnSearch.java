package com.springproject.core.model.Elastic.search;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnnSearch {
    private String field;
    private Integer k = 10;
    private Integer numCandidates = 10;
    private List<Float> query_vector;
}
