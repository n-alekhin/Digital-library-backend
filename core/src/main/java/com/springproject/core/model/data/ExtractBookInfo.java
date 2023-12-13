package com.springproject.core.model.data;

import com.springproject.core.model.data.Elastic.ElasticPartChapter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExtractBookInfo {
    private String mediaType;
    private String title;
    private List<String> authors;
    private String language;
    private String genres;
    private String description;
    private String publisher;
    private Long size;
    private List<ElasticPartChapter> chapters;
    private byte[] coverImage;
}
