package com.springproject.core.model.data.Elastic;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "book2")
public class ElasticBook {
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String publisher;
    @Field(type = FieldType.Text)
    private List<String> authors;
    @Field(type = FieldType.Nested)
    private List<ElasticPartChapter> chapters;
    @Field(type = FieldType.Integer)
    private Integer reviews;

}
