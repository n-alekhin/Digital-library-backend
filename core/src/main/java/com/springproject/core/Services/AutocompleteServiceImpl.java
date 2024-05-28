package com.springproject.core.Services;

import com.springproject.core.Repository.BookRepository;
import com.springproject.core.model.Entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutocompleteServiceImpl implements AutocompleteService{
    private final BookRepository bookRepository;
    public List<String> autocompleteTitle(String part) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(part);
        List<String> titles = new ArrayList<>();
        for (Book book : books) {
            titles.add(book.getTitle());
        }
        return titles.stream()
                .sorted(Comparator.comparingInt(title -> title.toLowerCase().indexOf(part.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<String> autocompleteAuthors(String authorsInput) {
        String[] authors = authorsInput.split(",\\s*");
        String lastAuthor = authors[authors.length - 1].trim();
        List<String> authorLines = bookRepository.findAuthorsContaining(lastAuthor);

        return authorLines.stream()
                .map(line -> Arrays.asList(line.split(",\\s*")))
                .flatMap(List::stream)
                .distinct()
                .filter(name -> {
                    String lowerCaseName = name.trim().toLowerCase();
                    String lowerCaseLastAuthor = lastAuthor.toLowerCase();
                    return lowerCaseName.startsWith(lowerCaseLastAuthor) || lowerCaseName.contains(" " + lowerCaseLastAuthor);
                })
                .sorted(Comparator.comparing((String name) -> !name.trim().toLowerCase().startsWith(lastAuthor.toLowerCase())))
                .collect(Collectors.toList());
    }

}
