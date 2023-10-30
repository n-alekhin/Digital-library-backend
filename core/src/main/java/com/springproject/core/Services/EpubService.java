package com.springproject.core.Services;

import com.springproject.core.Entity.Elastic.ElasticBook;
import com.springproject.core.Entity.Elastic.ElasticChapter;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpubService {

    private String removeHtmlTags(String html) {
        return html == null ? null : html.replaceAll("<[^>]*>", "");
    }

    public ElasticBook extractInfoFromEpub(InputStream epubStream) {
        ElasticBook elasticBook = new ElasticBook();

        try {
            Book book = (new EpubReader()).readEpub(epubStream);

            elasticBook.setTitle(book.getTitle());
            elasticBook.setPublisher(book.getMetadata().getPublishers().get(0));

            List<String> authors = book.getMetadata().getAuthors().stream()
                    .map(Author::toString)
                    //.map(ElasticAuthor::new)
                    .collect(Collectors.toList());
            elasticBook.setAuthors(authors);

            List<ElasticChapter> chapters = book.getSpine().getSpineReferences().stream()
                    .map(ref -> {
                        Resource res = ref.getResource();
                        try {
                            return new ElasticChapter(res.getTitle(),
                                    removeHtmlTags(new String(res.getData(), StandardCharsets.UTF_8)));
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при чтении ресурса", e);
                        }
                    }).collect(Collectors.toList());
            elasticBook.setChapters(chapters);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении EPUB", e);
        }

        return elasticBook;
    }
}
