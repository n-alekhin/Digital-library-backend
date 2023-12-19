package com.springproject.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class BookDTO {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "Harry Potter and the Half-Blood Prince")
    private String title;
    @Schema(example = "J.K. Rowling")
    private String authors;
    @Schema(example = "http://localhost:8080/book/cover/1")
    private String coverImageUrl;
}
