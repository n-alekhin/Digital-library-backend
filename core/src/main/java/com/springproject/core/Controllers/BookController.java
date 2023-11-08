package com.springproject.core.Controllers;

import com.springproject.core.Entity.BookFullInfo;
import com.springproject.core.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.CoverImageRepository;
import com.springproject.core.dto.BookDTO;
import com.springproject.core.dto.DetailedBookDTO;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.model.Constants;
import com.springproject.core.model.Elastic.BoolSearch;
import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.SearchService;
import com.springproject.core.dto.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final Constants constants;
    private final AttachmentService attachmentService;
    private final SearchService searchService;
    private final CoverImageRepository coverImageRepository;
    private final BookFullInfoRepository bookFullInfoRepository;
    private final ModelMapper modelMapper;



    @GetMapping("/search/advanced")
    public List<BookDTO> searchBookAdvanced(@RequestBody BoolSearch boolSearch) {
        return searchService.searchBookBool(boolSearch);
    }
    @GetMapping("/{bookId}")
    public DetailedBookDTO getDetailedBook(@PathVariable String bookId) {
        BookFullInfo bookFullInfo = bookFullInfoRepository.findById(Long.parseLong(bookId)).orElseThrow(() -> new BookNotFoundException("Book not found"));
        DetailedBookDTO book = modelMapper.map(bookFullInfo, DetailedBookDTO.class);
        book.setCoverImageUrl(constants.getImagePath() + bookFullInfo.getBook().getId());
        book.setTitle(bookFullInfo.getBook().getTitle());
        book.setAuthors(bookFullInfo.getBook().getAuthors());
        return book;
    }
    @Transactional
    @GetMapping("/cover/{bookId}")
    public ResponseEntity<Resource> getImageBook(@PathVariable String bookId) {
        CoverImage image = coverImageRepository.getReferenceById(Long.parseLong(bookId));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getMediaType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"cover" +
                                bookId + "." + image.getMediaType().substring(image.getMediaType().indexOf('/') + 1)
                                + "\"")
                .body(new ByteArrayResource(image.getCoverImage()));
    }
    @PostMapping("/load")
    public String loadBook(@RequestParam("book") MultipartFile bookEpub) throws Exception {
        if (bookEpub.isEmpty()) {
            return "File not found";
        }
        attachmentService.saveBookEpub(bookEpub);
        return "Success";
    }
    @GetMapping("/download/{bookId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long bookId) throws Exception {
        Attachment attachment = attachmentService.getAttachment(bookId);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(constants.type))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getName()
                                + "\"")
                .body(new ByteArrayResource(attachment.getBody()));
    }
}

/*@GetMapping("/search/authors")
    public List<ElasticBook> searchBook(@RequestParam String query) {
        List<ElasticBook> books = new LinkedList<>();
        Query query2 = new NativeQueryBuilder()
                .withQuery(q1 -> q1.nested(n -> n
                                .path("authors")
                                .query(q -> q
                                        .match(m -> m
                                                .field("authors.author")
                                                .query(query)
                                        )
                                )
                        )
                ).build();
        query2.setFields(Arrays.asList("title", "id"));
        query2.setStoredFields(Arrays.asList("title", "id"));
        operations.search(query2, ElasticBook.class).getSearchHits().forEach(h -> books.add(h.getContent()));
        return books;
    }*/
/*@GetMapping("/search/chapters")
public List<ElasticBook> searchBookByChapters(@RequestParam String query) {
    List<ElasticBook> books = new LinkedList<>();
    Query query2 = new NativeQueryBuilder()
            .withQuery(q1 -> q1.nested(n -> n
                            .path("chapters")
                            .query(q -> q
                                    .match(m -> m
                                            .field("chapters.content")
                                            .query(query)
                                            .operator(Operator.And)
                                    )
                            )
                    )
            ).build();
    operations.search(query2, ElasticBook.class).getSearchHits().forEach(h -> books.add(h.getContent()));
    return books;
}*/
