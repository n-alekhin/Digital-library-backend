package com.springproject.core.Controllers;

import com.springproject.core.model.Elastic.ElasticBook;
import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.dto.domain.JwtAuthentication;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UtilityController {

  private final AuthService authService;
  private final AttachmentService attachmentService;
  private final ElasticsearchOperations operations;

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

  @PostMapping("/loadAllBooks")
  public void loadAll() throws IOException {
    String path = "C:\\Users\\User\\Desktop\\Study\\Digital Library\\books\\"; // директория, где лежат книги
    File directory = new File(path);
    if (directory.isDirectory()) {
      String[] fileNames = directory.list();
      if (fileNames != null) {
        for (String fileName : fileNames) {
          Path filePath = Paths.get(path + fileName);
          File tmp = new File(path + fileName);
          attachmentService.saveBookForTesting(fileName, Files.newInputStream(filePath), Files.newInputStream(filePath), tmp.length(), "0");
        }
      }
    }
  }

}