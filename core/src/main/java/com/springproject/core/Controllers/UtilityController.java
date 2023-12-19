package com.springproject.core.Controllers;

import com.springproject.core.Services.attachment.AttachmentServiceImpl;
import com.springproject.core.Services.search.SearchService;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.dto.KnnAndBoolSearch;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import com.springproject.core.model.data.Elastic.search.Knn;
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
  @GetMapping("/knn")
  public List<BookDTO> knnSearch(@RequestBody Knn knn) {
    return searchService.searchBookKnn(knn);
  }
  @GetMapping("/knn-and-bool")
  public List<BookDTO> knnSearch(@RequestBody KnnAndBoolSearch query) {
    return searchService.searchBookKnnAndBool(query.getKnn(), query.getBool(), query.getBoostKnn());
  }

  @PostMapping("/loadAllBooks")
  public void loadAll() throws IOException {
    //String path = "C:\\Users\\User\\Desktop\\Study\\Digital Library\\books\\"; // директория, где лежат книги
    String path = "D:\\MiniProjects\\parseBook\\books\\";
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

}