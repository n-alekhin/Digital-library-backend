package com.springproject.core.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnnDTO {
    private String query;
    private Integer k = 20;
    private Float boost = 5f;
    private Integer numCandidates = 30;
}
