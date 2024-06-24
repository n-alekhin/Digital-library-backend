package com.springproject.core.Controllers;

import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Services.attachment.AttachmentServiceImpl;
import com.springproject.core.Services.search.SearchService;
import com.springproject.core.model.Entity.Book;
import com.springproject.core.model.Entity.BookFullInfo;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.model.dto.DetailedBookDTO;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import com.springproject.core.model.dto.KnnDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Hidden
public class UtilityController {

  private final AuthService authService;
  private final AttachmentServiceImpl attachmentService;
  private final ElasticsearchOperations operations;
  private final SearchService searchService;
  private final BookFullInfoRepository bookFullInfoRepository;
  private final BookRepository bookRepository;

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
  @PostMapping("/delete-index")
  public Boolean deleteIndex() {
    return operations.indexOps(ElasticBook.class).delete();
  }
  @GetMapping("/knn-and-bool")
  public List<BookDTO> knnAndBoolSearch(@RequestBody KnnDTO query) {
    return searchService.searchBookKnnExpanded(query);
  }

  @PostMapping("/loadAllBooks")
  public void loadAll() throws IOException {
    String path = "C:\\Users\\User\\Desktop\\Study\\Digital Library\\books2\\"; // директория, где лежат книги
    //String path = "C:/Users/1/Downloads/books2/books2/";
    File directory = new File(path);
    if (directory.isDirectory()) {
      String[] fileNames = directory.list();
      if (fileNames != null) {
        for (String fileName : fileNames) {
          System.out.println(fileName);
          Path filePath = Paths.get(path + fileName);
          File tmp = new File(path + fileName);
          attachmentService.saveBookForTesting(fileName, Files.newInputStream(filePath), Files.newInputStream(filePath), tmp.length(), "0");
        }
      }
    }
  }
  @PostMapping("/change/{id}/description")
  public void changeDescription(@PathVariable Long id, @RequestBody DetailedBookDTO description) {
    BookFullInfo book = bookFullInfoRepository.findById(id).orElseThrow();
    book.setDescription(description.getDescription());
    bookFullInfoRepository.save(book);
  }
  @PostMapping("/change/{id}/title")
  public void changeTitle(@PathVariable Long id, @RequestBody DetailedBookDTO description) {
    Book book = bookRepository.findById(id).orElseThrow();
    book.setTitle(description.getTitle());
    bookRepository.save(book);
  }
  @GetMapping("/hi")
  public String sayHi() {
    return "Hi!";
  }

}