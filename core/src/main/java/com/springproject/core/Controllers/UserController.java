package com.springproject.core.Controllers;

import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.Services.EmailService;
import com.springproject.core.Services.UserService;
import com.springproject.core.model.dto.ChangeNotificationPolicyDTO;
import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.UserDtoResponse;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import jakarta.validation.Valid;
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
    private final AuthService authService;

    @GetMapping("getUser")
    public ResponseEntity<UserDtoResponse> getUser(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping("/reg")
    public ResponseEntity<?> create(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto, 0);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/regAdmin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto, 1);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/regAdminS")
    public ResponseEntity<?> createAdminS(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto, 2);
        return ResponseEntity.ok().build();
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
    @PostMapping("/notification")
    public void changeNotificationPolicy(@Valid @RequestBody ChangeNotificationPolicyDTO changeNotificationPolicyDTO) {
        userService.changeNotificationPolicy(authService.getAuthInfo().getId(),
                changeNotificationPolicyDTO.getIsSendNotification());
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
