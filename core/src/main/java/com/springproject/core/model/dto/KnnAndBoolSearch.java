package com.springproject.core.model.dto;

import com.springproject.core.model.data.Elastic.search.BoolSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KnnAndBoolSearch {
    private KnnDTO knnDTO;
    private Float boostKnn = 1f;
    private BoolSearch bool;
}
