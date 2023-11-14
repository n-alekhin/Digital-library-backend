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
    @Column(length = 10000)
    private String description;
    private String publisher;
    private Long size;

    @Override
    public String toString() {
        return "Title: " + book.getTitle() +
                "\nAuthors: " + book.getAuthors() +
                "\nGenres: " + genres +
                "\nPublisher: " + publisher +
                "\nLanguage: " + language;
    }

    public BookFullInfo(Book book) {
        this.book = book;
    }
}
