package com.springproject.core.Controllers;

import com.springproject.core.Services.EmailService;
import com.springproject.core.Services.UserService;
import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.UserDtoResponse;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import com.springproject.core.model.dto.domain.JwtRequest;
import com.springproject.core.model.dto.domain.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final EmailService emailService;
    private final UserService userService;

    @GetMapping("getUser")
    public ResponseEntity<UserDtoResponse> getUser(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping("/reg")
    public ResponseEntity<JwtResponse> create(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto, 0));
    }

    @PostMapping("/regAdmin")
    public ResponseEntity<JwtResponse> createAdmin(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto, 1));
    }

    @PostMapping("/regAdminS")
    public ResponseEntity<JwtResponse> createAdminS(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto, 2));
    }

    @PostMapping("/banAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Long> banAdmin(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.banAdmin(userId));
    }

    @PostMapping("/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> ban(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.ban(userId));
    }

    @PostMapping("/grantAdminRights")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Long> grantAdminRights(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.grantAdminRights(userId));
    }

    @PostMapping("/revokeAdminRights")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Long> revokeAdminRights(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.revokeAdminRights(userId));
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public void test() {
        System.out.println(((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getAuthorities());
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        // System.out.println(((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()). );
    }

    @GetMapping("/test2")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> test2() {
        String[] recipients = {"n.alekhin@g.nsu.ru","e.pashko@g.nsu.ru"};
        System.out.println("321");
        emailService.sendEmail(recipients, "Welcome!", "This is a test email from our Spring Boot application.");
        System.out.println("321");
        return ResponseEntity.ok("Email sent successfully");
    }
}
