package com.springproject.core.model.dto;

import lombok.Data;

@Data
public class RequestChunks {
    private final String input;
    private final Boolean vectorSearch;
}
