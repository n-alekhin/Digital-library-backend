package com.springproject.core.model.Elastic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "book")
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
    private List<ElasticChapter> chapters;

}
