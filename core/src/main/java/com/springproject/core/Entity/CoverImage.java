package com.springproject.core.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoverImage implements Serializable {
    @Id
    private Long id;
    @OneToOne
    @MapsId
    private Book book;
    private String mediaType;

    @Lob
    private byte[] coverImage;

    public CoverImage(Book book) {
        this.book = book;
    }
}
