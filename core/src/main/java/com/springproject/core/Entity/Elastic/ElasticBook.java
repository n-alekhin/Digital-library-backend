package com.springproject.core.Entity.Elastic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@Document(indexName = "book")
public class ElasticBook {
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String publisher;
    @Field(type = FieldType.Nested)
    private List<ElasticAuthor> authors;
    @Field(type = FieldType.Nested)
    private List<ElasticChapter> chapters;
}
