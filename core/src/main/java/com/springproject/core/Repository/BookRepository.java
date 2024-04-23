package com.springproject.core.Repository;

import com.springproject.core.model.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByFileName(String fileName);
    @Query("SELECT c FROM Book c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT DISTINCT b.authors FROM Book b WHERE LOWER(b.authors) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<String> findAuthorsContaining(String author);
}
