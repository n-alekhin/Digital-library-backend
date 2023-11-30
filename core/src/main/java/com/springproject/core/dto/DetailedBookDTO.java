package com.springproject.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailedBookDTO {
    @Schema(example = "Harry Potter and the Chamber of Secrets")
    private String title;
    @Schema(example = "J.K. Rowling")
    private String authors;
    @Schema(example = "en")
    private String language;
    @Schema(example = "null")
    private String genres;
    @Schema(example = "null")
    private String description;
    @Schema(example = "Pottermore Publishing")
    private String publisher;
    @Schema(example = "http://localhost:8080/book/cover/2")
    private String coverImageUrl;
}
