package com.springproject.core.model.data.Elastic.search;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Knn {
    private String query;
    private Integer k = 10;
    private Float boost = 5f;
    private Integer numCandidates = 10;
}
