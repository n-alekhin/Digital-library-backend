package com.springproject.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EpubDto {
    private String title;
    private List<String> authors;
    private List<String> chapterContents;
}
