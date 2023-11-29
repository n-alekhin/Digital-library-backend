package com.springproject.core.Services;

import com.springproject.core.dto.BookDTO;
import com.springproject.core.model.Elastic.search.BoolSearch;
import com.springproject.core.model.Elastic.search.KnnSearch;

import java.util.List;

public interface SearchService {
    List<BookDTO> searchBookBool(BoolSearch query);
    List<BookDTO> searchBookKnn(KnnSearch query);
    List<BookDTO> searchBookKnnAndBool(KnnSearch knnQuery, BoolSearch boolQuery);
}
