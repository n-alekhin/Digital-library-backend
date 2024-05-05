package com.springproject.core.Controllers;

import com.springproject.core.Services.ReviewService;
import com.springproject.core.model.dto.ReviewDTO;
import com.springproject.core.model.dto.ReviewDtoOutput;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping("/create/{idBook}")
    public Long create(
            @RequestBody ReviewDTO reviewDTO,
            @PathVariable Long idBook
    ) {
        Long idUser = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getId();
        return reviewService.createReview(reviewDTO, idBook, idUser);
    }

    @GetMapping("/getBook/{idBook}")
    public List<ReviewDtoOutput> getReviewBook(
            @PathVariable Long idBook
    ) {
        return reviewService.getReviewBook(idBook);
    }

    @GetMapping("/getUser/{idUser}")
    public List<ReviewDtoOutput> getReviewUser(
            @PathVariable Long idUser
    ) {
        return reviewService.getReviewUser(idUser);
    }

    @GetMapping("/getMeanGrade/{idBook}")
    public Double getMeanGrade(
            @PathVariable Long idBook
    ) throws Exception {
        return reviewService.getMeanGrade(idBook);
    }
}
