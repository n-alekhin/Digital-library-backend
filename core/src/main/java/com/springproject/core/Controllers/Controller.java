package com.springproject.core.Controllers;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.springproject.core.Entity.Book;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.dto.BookDTO;
import com.springproject.core.model.Constants;
import com.springproject.core.model.Elastic.BoolSearch;
import com.springproject.core.model.Elastic.ElasticBook;
import com.springproject.core.model.Elastic.ElasticMathQuery;
import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.Services.SearchService;
import com.springproject.core.dto.domain.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

  private final Constants constants;
  private final AuthService authService;
  private final AttachmentService attachmentService;
  private final ElasticsearchOperations operations;
  private final BookRepository bookRepository;
  private final SearchService searchService;
  private final ModelMapper modelMapper;

  @PreAuthorize("hasAuthority('USER')")
  @GetMapping("/hello")
  public ResponseEntity<String> helloUser() {
    final JwtAuthentication authInfo = authService.getAuthInfo();
    return ResponseEntity.ok("Hello user " + authInfo.getPrincipal() + "!");
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/hello/admin")
  public ResponseEntity<String> helloAdmin() {
    final JwtAuthentication authInfo = authService.getAuthInfo();
    return ResponseEntity.ok("Hello admin " + authInfo.getPrincipal() + "!");
  }
  @GetMapping("/search/authors")
  public List<BookDTO> searchBook(@RequestParam String query) {
    Map<String, ElasticMathQuery> must = new HashMap<>();
    must.put("authors", new ElasticMathQuery(query, Operator.Or));
    return searchService.searchBookBool(BoolSearch.builder().must(must).build());
  }
  @GetMapping("/search/chapters")
  public List<BookDTO> searchBookByChapters(@RequestParam String query) {
    Map<String, ElasticMathQuery> must = new HashMap<>();
    must.put("chapters.content", new ElasticMathQuery(query, Operator.And));
    return searchService.searchBookBool(BoolSearch.builder().must(must).build());
  }
  @PostMapping("/delete-index")
  public Boolean deleteIndex() {
    return operations.indexOps(ElasticBook.class).delete();
  }
  @GetMapping("/search/title")
  public List<BookDTO> searchBookByTitle(@RequestParam String query) {
    Map<String, ElasticMathQuery> must = new HashMap<>();
    must.put("title", new ElasticMathQuery(query, Operator.Or));
    return searchService.searchBookBool(BoolSearch.builder().must(must).build());
  }
  @GetMapping("/book")
  public BookDTO getFirstBook() {
    Book bookDB = bookRepository.getReferenceById(1L);
    BookDTO book = modelMapper.map(bookDB, BookDTO.class);
    book.setCoverImageUrl(constants.getImagePath() + bookDB.getId());
    return book;
  }
  @PostMapping("/loadAllBooks")
  public void loadAll() throws IOException {
    String path = "C:\\Users\\User\\Desktop\\Study\\Digital Library\\books\\"; // директория, где лежат книги
    File directory = new File(path);
    if (directory.isDirectory()) {
      String[] fileNames = directory.list();
      if (fileNames != null) {
        for (String fileName : fileNames) {
          System.out.println(fileName);
          Path filePath = Paths.get(path + fileName);
          File tmp = new File(path + fileName);
          attachmentService.saveBookForTesting(fileName, Files.newInputStream(filePath), Files.newInputStream(filePath), tmp.length());
        }
      }
    }
  }

}