package com.springproject.core.Services;

import com.springproject.core.dto.BookDTO;
import com.springproject.core.model.Elastic.BoolSearch;

import java.util.List;

public interface SearchService {
    List<BookDTO> searchBookBool(BoolSearch query);
}
