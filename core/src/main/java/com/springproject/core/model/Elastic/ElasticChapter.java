package com.springproject.core.model.Elastic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticChapter {
    @Field(name = "vector", dims = 384, index = true, type = FieldType.Dense_Vector)
    private double[] vector;

    @Field(type = FieldType.Text)
    private String content;
}
