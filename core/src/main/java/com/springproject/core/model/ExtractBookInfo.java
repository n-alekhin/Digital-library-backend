package com.springproject.core.model;

import com.springproject.core.model.Elastic.ElasticChapter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExtractBookInfo {
    private String mediaType;
    private String title;
    private String authors;
    private String language;
    private String genres;
    private String description;
    private String publisher;
    private Long size;
    private List<ElasticChapter> chapters;
    private byte[] coverImage;
}
