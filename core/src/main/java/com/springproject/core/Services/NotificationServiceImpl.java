package com.springproject.core.Services;

import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Services.search.SearchService;
import com.springproject.core.model.Entity.Book;
import com.springproject.core.model.Entity.Review;
import com.springproject.core.model.Entity.User;
import com.springproject.core.model.data.BookScore;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.data.Elastic.ElasticPartChapter;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SearchService searchService;
    private final BookRepository bookRepository;
    private final EmailService emailService;
    @Value("${application.client-host}")
    private String clientHost;
    @Override
    @Async
    public void sendNotification(ElasticBook elasticBook, Long providerId) {
        TreeSet<BookScore> scores = new TreeSet<>(Comparator.comparing(BookScore::getBookId));
        elasticBook.getChapters().parallelStream().forEach(chapter -> {
            KnnSearch query = new KnnSearch();
            query.setQuery_vector(chapter.getVector());
            List<SearchHit<ElasticBook>> hits = searchService.searchBookHitsKnn(query);
            synchronized (scores) {
                for (SearchHit<ElasticBook> hit: hits) {
                    BookScore bookScore = scores.ceiling(new BookScore(hit.getContent().getId(), 0));
                    if (bookScore == null || bookScore.getBookId() != hit.getContent().getId()) {
                        scores.add(new BookScore(hit.getContent().getId(), hit.getScore()));
                    } else {
                        bookScore.setScore(bookScore.getScore() + hit.getScore());
                    }
                }
            }
        });
        int size = elasticBook.getChapters().size();
        scores.forEach(s -> log.info(s.getBookId() + ", scores: " + (s.getScore() / size)));
        List<Long> ids = scores.stream().filter(s -> s.getScore() / size > 0.80).map(BookScore::getBookId).collect(Collectors.toList());
        List<Book> books = bookRepository.findAllById(ids);
        books.forEach(b -> log.info(b.getId() + ", title: " + b.getTitle()));
        List<User> tmp = bookRepository.findUserLoginsByBooks(books);
        List<User> confirmedUsers = tmp.stream().filter(u -> u.getIsConfirmed() && !Objects.equals(u.getId(), providerId) &&
                u.getIsSendNotification()).toList();
        confirmedUsers.forEach(u -> log.info(u.getLogin()));
        String[] to = confirmedUsers.stream().map(User::getLogin).toList().toArray(new String[0]);
        if (to.length > 0) {
            emailService.sendEmail(to,
                    //"Book recommendation", "The book has been uploaded that may interest you!\n You can watch it here " +
                    "Book recommendation",
                    "Hello, dear user. We have a new recommendation for you! \nIf you have free time, you can view this book at the link " +
                            clientHost + "/foundbooks/" + elasticBook.getId());
        }
    }

}
