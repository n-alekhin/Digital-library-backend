package com.springproject.core.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookScore {
    private long bookId;
    private float score;
}
