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
    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private List<String> authors;
    @Field(type = FieldType.Object)
    private List<ElasticChapter> chapters;
    @Field(name = "my_vector", dims = 384, index = true, type = FieldType.Dense_Vector)
    private double[] myVector;

}
