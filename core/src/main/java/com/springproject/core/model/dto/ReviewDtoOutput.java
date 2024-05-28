package com.springproject.core.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDtoOutput {
    public Long id;
    public Integer grade;
    public String comment;
    public Long idBook;
    public ShortUserDTO user;
}
