package com.springproject.core.Entity.Elastic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

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
    /*@Field(type = FieldType.Nested)
    private List<ElasticAuthor> authors;
    @Field(type = FieldType.Nested)
    private List<ElasticChapter> chapters;*/
    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private List<String> authors;
    @Field(type = FieldType.Object)
    private List<ElasticChapter> chapters;
}
