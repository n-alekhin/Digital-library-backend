package com.springproject.core.Controllers;

import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.dto.domain.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

  private final AuthService authService;
  private final AttachmentService attachmentService;

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
          attachmentService.saveBookForTesting(fileName, Files.newInputStream(filePath),Files.newInputStream(filePath));
        }
      }
    }
  }

}