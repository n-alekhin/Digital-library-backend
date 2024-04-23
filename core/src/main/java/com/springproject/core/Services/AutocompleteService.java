package com.springproject.core.Services;

import java.util.List;

public interface AutocompleteService {
    List<String> autocompleteTitle(String part);
    List<String> autocompleteAuthors(String authors);
}
