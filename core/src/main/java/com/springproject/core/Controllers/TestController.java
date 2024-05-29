package com.springproject.core.Controllers;

import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.Services.ReviewService;
import com.springproject.core.Services.UserService;
import com.springproject.core.model.dto.ReviewDTO;
import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.domain.JwtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class TestController {


    private final UserService userService;
    private final ReviewService reviewService;
    private final AuthService authService;

    public TestController(UserService userService, ReviewService reviewService, AuthService authService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @PostMapping("/create-multiple")
    public void createMultipleUsers() {
        List<Long> userIds = new ArrayList<>(); // Список для хранения ID созданных пользователей
        String commonPassword = "commonPassword123"; // Общий пароль для всех пользователей
        for (int i = 0; i < 100; i++) {
            UserDto newUser = new UserDto();
            newUser.setLogin("user" + i + "@example.com");
            newUser.setName("User" + i);
            newUser.setPassword(commonPassword); // Установка общего пароля
            newUser.setIsSendNotification(false); // Уведомления не отправляются

            // Создание пользователя и сохранение его ID
            userService.createUser(newUser, 0);
        }
    }
    @PostMapping("/add-multiple")
    public ResponseEntity<String> addMultipleReviews(@RequestParam Long bookId) {
        List<Long> userIds = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            userIds.add(authService.login(new JwtRequest("user" + i + "@example.com", "commonPassword123")).getId());
        }
        for (Long userId : userIds) {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setGrade(Math.abs(random.nextInt() % 2) + 4);
            reviewDTO.setComment("Отличная книга! Очень рекомендую.");

            reviewService.createReview(reviewDTO, bookId, userId);
        }
        return ResponseEntity.ok("Комментарии успешно добавлены");
    }

}
