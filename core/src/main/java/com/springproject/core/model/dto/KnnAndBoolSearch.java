package com.springproject.core.model.dto;

import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KnnAndBoolSearch {
    private KnnSearch knn;
    private BoolSearch bool;
}
