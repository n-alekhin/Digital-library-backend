package com.springproject.core.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDtoOutput {
    public Integer grade;
    public String comment;
    public Long idBook;
    public Long idUser;
}
