package com.springproject.core.Services.search;

import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.model.data.Elastic.search.Knn;

import com.springproject.core.model.dto.WikidataSearchDTO;
import java.util.List;

public interface SearchService {
    List<BookDTO> searchBookBool(BoolSearch query);
    List<BookDTO> searchBookKnn(Knn query);
    List<BookDTO> searchBookKnnAndBool(Knn knn);

    List<BookDTO> wikidataSearch(WikidataSearchDTO wikidataSearchDTO );
}
