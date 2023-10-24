package com.springproject.core.Services;

import com.springproject.core.dto.EpubDto;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpubService {

    private String removeHtmlTags(String html) {
        return html == null ? null : html.replaceAll("<[^>]*>", "");
    }

    public EpubDto extractInfoFromEpub(InputStream epubStream) {
        EpubDto epubInfo = new EpubDto();

        try {
            Book book = (new EpubReader()).readEpub(epubStream);

            epubInfo.setTitle(book.getTitle());

            List<String> authors = book.getMetadata().getAuthors().stream()
                    .map(Author::toString)
                    .collect(Collectors.toList());
            epubInfo.setAuthors(authors);

            List<String> chapterContents = book.getSpine().getSpineReferences().stream()
                    .map(ref -> {
                        Resource res = ref.getResource();
                        try {
                            return new String(res.getData(), "UTF-8");
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при чтении ресурса", e);
                        }
                    })
                    .collect(Collectors.toList());
            List<String> chapterContentsRemove = new ArrayList<>();
            for (String chapter : chapterContents) {
                chapterContentsRemove.add(removeHtmlTags(chapter));
            }
            epubInfo.setChapterContents(chapterContentsRemove);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении EPUB", e);
        }

        return epubInfo;
    }
}
