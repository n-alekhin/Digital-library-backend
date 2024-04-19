package com.springproject.core.Services;

import com.springproject.core.Services.search.SearchService;
import com.springproject.core.model.data.BookScore;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.model.data.Elastic.ElasticPartChapter;
import com.springproject.core.model.data.Elastic.search.KnnSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SearchService searchService;
    @Override
    @Async
    public void sendNotification(List<ElasticPartChapter> chapters) {
        TreeSet<BookScore> scores = new TreeSet<>(Comparator.comparing(BookScore::getBookId));
        chapters.parallelStream().forEach(chapter -> {
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
        scores.forEach(s -> System.out.println(s.getBookId() + " --- " + s.getScore() / chapters.size()));
    }

}
