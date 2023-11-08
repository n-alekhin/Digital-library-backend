package com.springproject.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailedBookDTO {
    private String title;
    private String authors;
    private String language;
    private String genres;
    private String description;
    private String publisher;
    private Long size;
    private String coverImageUrl;
}
