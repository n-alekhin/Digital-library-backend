package com.springproject.core.model.data.Elastic.search;


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
    private String field = "chapters.vector";
    private Integer k = 20;
    private Integer numCandidates = 30;
    private List<Float> query_vector;
}
