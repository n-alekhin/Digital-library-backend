package com.springproject.core.Services;

import com.springproject.core.model.Elastic.ElasticChapter;
import com.springproject.core.model.ExtractBookInfo;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpubService {

    public ExtractBookInfo extractInfoFromEpub(InputStream epubStream) {
        ExtractBookInfo fullBookInfo = new ExtractBookInfo();

        try {
            Book book = (new EpubReader()).readEpub(epubStream);
            book.getCoverPage();
            fullBookInfo.setTitle(book.getTitle());
            fullBookInfo.setPublisher(book.getMetadata().getPublishers().stream().findFirst().orElse(""));

            List<String> authors = book.getMetadata().getAuthors().stream()
                    .map(a -> a.getFirstname() + " " + a.getLastname())
                    .collect(Collectors.toList());
            fullBookInfo.setAuthors(authors);

            List<ElasticChapter> chapters = book.getSpine().getSpineReferences().stream()
                    .map(ref -> {
                        Resource res = ref.getResource();
                        try {
                            return new ElasticChapter(res.getTitle(),
                                    Jsoup.parse(new String(res.getData(), StandardCharsets.UTF_8)).text());
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при чтении ресурса", e);
                        }
                    }).collect(Collectors.toList());
            fullBookInfo.setChapters(chapters);
            if (book.getMetadata().getSubjects() != null) {
                fullBookInfo.setGenres(book.getMetadata().getSubjects().stream().reduce((acc, s) -> acc.concat(", " + s)).orElse(null));
                if (fullBookInfo.getGenres() != null && fullBookInfo.getGenres().length() > 255) {
                    fullBookInfo.setGenres(null);
                }
            }

            fullBookInfo.setLanguage(book.getMetadata().getLanguage());
            String description = book.getMetadata().getDescriptions().stream().reduce((acc, s) -> acc.concat(". " + s)).orElse(null);
            fullBookInfo.setDescription(description);
            if (book.getCoverImage() != null) {
                fullBookInfo.setCoverImage(book.getCoverImage().getData());
                fullBookInfo.setMediaType(book.getCoverImage().getMediaType().toString());
            } else if (book.getCoverPage() != null) {
                fullBookInfo.setCoverImage(book.getCoverPage().getData());
                fullBookInfo.setMediaType(book.getCoverPage().getMediaType().toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении EPUB", e);
        }

        return fullBookInfo;
    }

}
