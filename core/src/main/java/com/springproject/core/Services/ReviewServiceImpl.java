package com.springproject.core.Services;

import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Repository.ElasticBookRepository;
import com.springproject.core.Repository.ReviewRepository;
import com.springproject.core.Repository.UserRepository;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.exceptions.IsEmptyException;
import com.springproject.core.model.Entity.Book;
import com.springproject.core.model.Entity.Review;
import com.springproject.core.model.Entity.User;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.dto.ReviewDTO;
import com.springproject.core.model.dto.ReviewDtoOutput;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ElasticBookRepository elasticBookRepository;
    public Long createReview(ReviewDTO reviewDTO, Long idBook, Long idUser) {
        Review review = new Review();
        Book book = bookRepository.findById(idBook).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        review.setBook(book);
        User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException("User not found"));
        review.setUser(user);
        review.setComment(reviewDTO.getComment());
        review.setGrade(reviewDTO.getGrade());
        reviewRepository.save(review);
        book.getReview().add(review);
        user.getReview().add(review);
        userRepository.save(user);
        bookRepository.save(book);
        ElasticBook elasticBook = elasticBookRepository.findById(idBook)
                .orElseThrow(() -> new EntityNotFoundException("Elastic book not found"));
        elasticBook.setReviews(elasticBook.getReviews() + 1);
        elasticBookRepository.save(elasticBook);
        return review.getId();
    }

    public List<ReviewDtoOutput> getReviewBook(Long idBook) {
        Book book = bookRepository.findById(idBook).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        List<ReviewDtoOutput> reviewDtoOutputs = new ArrayList<>();
        for (Review review : book.getReview()) {
            ReviewDtoOutput reviewDtoOutput = new ReviewDtoOutput();
            reviewDtoOutput.comment = review.getComment();
            reviewDtoOutput.grade = review.getGrade();
            reviewDtoOutput.idBook = review.getBook().getId();
            reviewDtoOutput.idUser = review.getUser().getId();
            reviewDtoOutputs.add(reviewDtoOutput);
        }
        return reviewDtoOutputs;
    }

    public List<ReviewDtoOutput> getReviewUser(Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<ReviewDtoOutput> reviewDtoOutputs = new ArrayList<>();
        for (Review review : user.getReview()) {
            ReviewDtoOutput reviewDtoOutput = new ReviewDtoOutput();
            reviewDtoOutput.comment = review.getComment();
            reviewDtoOutput.grade = review.getGrade();
            reviewDtoOutput.idBook = review.getBook().getId();
            reviewDtoOutput.idUser = review.getUser().getId();
            reviewDtoOutputs.add(reviewDtoOutput);
        }
        return reviewDtoOutputs;
    }

    public Double getMeanGrade(Long idBook) throws RuntimeException {
        Book book = bookRepository.findById(idBook).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        Double sumGrade = 0.0;
        if (book.getReview().isEmpty()) {
            return -1.0;
        } else {
            for(Review review : book.getReview()) {
                sumGrade += review.getGrade();
            }
            return sumGrade / book.getReview().size();
        }
    }
}
