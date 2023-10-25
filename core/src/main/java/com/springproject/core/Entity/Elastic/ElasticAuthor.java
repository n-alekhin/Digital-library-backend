package com.springproject.core.Entity.Elastic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticAuthor {
    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private String author;
}
