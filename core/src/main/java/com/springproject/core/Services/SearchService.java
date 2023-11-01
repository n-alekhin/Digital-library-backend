package com.springproject.core.Services;

import com.springproject.core.Entity.Elastic.BoolSearch;
import com.springproject.core.Entity.Elastic.ElasticBook;

import java.util.List;

public interface SearchService {
    List<ElasticBook> searchBookBool(BoolSearch query);
}
