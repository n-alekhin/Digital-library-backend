package com.springproject.core.Repository;

import com.springproject.core.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByFileName(String fileName);
}
