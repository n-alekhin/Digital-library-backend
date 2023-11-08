package com.springproject.core.Services;

import com.springproject.core.model.Elastic.ElasticChapter;
import com.springproject.core.model.ExtractBookInfo;
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

    public ExtractBookInfo extractInfoFromEpub(InputStream epubStream) {
        ExtractBookInfo fullBookInfo = new ExtractBookInfo();

        try {
            Book book = (new EpubReader()).readEpub(epubStream);
            book.getCoverPage();
            fullBookInfo.setTitle(book.getTitle());
            fullBookInfo.setPublisher(book.getMetadata().getPublishers().stream().findFirst().orElse(""));

            List<String> authors = book.getMetadata().getAuthors().stream()
                    .map(Author::toString)
                    .collect(Collectors.toList());
            fullBookInfo.setAuthors(authors.stream().reduce((acc, s) -> acc.concat(", " + s)).orElse(null));

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
            fullBookInfo.setChapters(chapters);
            fullBookInfo.setGenres(book.getMetadata().getDescriptions().stream().reduce((acc, s) -> acc.concat(", " + s)).orElse(null));
            fullBookInfo.setLanguage(book.getMetadata().getLanguage());
            fullBookInfo.setDescription(book.getMetadata().getDescriptions().stream().reduce((acc, s) -> acc.concat(", " + s)).orElse(null));
            fullBookInfo.setCoverImage(book.getCoverImage().getData());

            fullBookInfo.setMediaType(book.getCoverImage().getMediaType().toString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении EPUB", e);
        }

        return fullBookInfo;
    }

}
