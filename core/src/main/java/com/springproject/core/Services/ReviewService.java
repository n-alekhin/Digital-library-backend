package com.springproject.core.Services;

import com.springproject.core.model.dto.ErrorMessageDTO;
import com.springproject.core.model.dto.ReviewDTO;
import com.springproject.core.model.dto.ReviewDtoOutput;

import java.util.List;

public interface ReviewService {
    Long createReview(ReviewDTO reviewDTO, Long idBook, Long idUser);
    List<ReviewDtoOutput> getReviewBook(Long idBook, int size, int page);
    List<ReviewDtoOutput> getReviewUser(Long idUser);
    Double getMeanGrade(Long idBook) throws Exception;
    ErrorMessageDTO deleteReview(Long id);
    boolean infReview(Long idBook, Long idUser);
    ErrorMessageDTO deleteAllByUser(Long idUser);
}
