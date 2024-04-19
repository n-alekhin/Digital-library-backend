package com.springproject.core.Services.search;

import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.model.dto.KnnDTO;

import com.springproject.core.model.dto.WikidataSearchDTO;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface SearchService {
    List<BookDTO> searchBookBool(BoolSearch query);
    List<BookDTO> searchBookKnn(KnnDTO query);
    List<BookDTO> searchBookKnnExpanded(KnnDTO knnDTO);

    List<BookDTO> wikidataSearch(WikidataSearchDTO wikidataSearchDTO);
    List<SearchHit<ElasticBook>> searchBookHitsKnn(KnnSearch query);
}
