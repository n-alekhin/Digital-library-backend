package com.springproject.core.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BookFullInfo {
    @Id
    private Long id;
    @OneToOne
    @MapsId
    private Book book;
    private String language;
    private String genres;
    private String description;
    private String publisher;
    private Long size;
}
