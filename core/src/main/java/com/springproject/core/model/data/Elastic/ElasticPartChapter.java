package com.springproject.core.model.data.Elastic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticPartChapter {
    @Field(name = "vector", dims = 768, index = true, type = FieldType.Dense_Vector, similarity = "dot_product")
    private List<Float> vector;

    @Field(type = FieldType.Text)
    private String content;
    public ElasticPartChapter(String content) {
        this.content = content;
    }
}
