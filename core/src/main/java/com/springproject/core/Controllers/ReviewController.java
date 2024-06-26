package com.springproject.core.Controllers;

import com.springproject.core.Repository.ReviewRepository;
import com.springproject.core.Services.ReviewService;
import com.springproject.core.model.dto.ErrorMessageDTO;
import com.springproject.core.model.dto.ReviewDTO;
import com.springproject.core.model.dto.ReviewDtoOutput;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;


    @PostMapping("/create/{idBook}")
    @PreAuthorize("hasRole('USER')")
    public Long create(
            @RequestBody ReviewDTO reviewDTO,
            @PathVariable Long idBook
    ) {
        Long idUser = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getId();
        return reviewService.createReview(reviewDTO, idBook, idUser);
    }

    @GetMapping("/getBook/{idBook}")
    public List<ReviewDtoOutput> getReviewBook(
            @PathVariable Long idBook,
            @RequestParam Integer size,
            @RequestParam Integer page
    ) {
        return reviewService.getReviewBook(idBook, size, page);
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

    @DeleteMapping("/delete/{idReview}")
    @PreAuthorize("hasRole('ADMIN')")
    public ErrorMessageDTO deleteReview(@PathVariable Long idReview) {
        return reviewService.deleteReview(idReview);
    }

    @GetMapping("/getInf")
    public boolean getInf(
            @RequestParam("Book") Long idBook,
            @RequestParam("User") Long idUser
    ) {
        return reviewService.infReview(idBook, idUser);
    }

    @DeleteMapping("/deleteAll/{idUser}")
    @PreAuthorize("hasRole('ADMIN')")
    public ErrorMessageDTO deleteAllReviewByUser(@PathVariable Long idUser) {
        return reviewService.deleteAllByUser(idUser);
    }

}
