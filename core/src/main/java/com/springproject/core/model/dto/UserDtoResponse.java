package com.springproject.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDtoResponse {
    private String name;
    private String role;
}
