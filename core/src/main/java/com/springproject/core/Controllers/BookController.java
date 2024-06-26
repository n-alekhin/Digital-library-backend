package com.springproject.core.Controllers;

import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Services.attachment.AttachmentService;
import com.springproject.core.model.Entity.Book;
import com.springproject.core.model.Entity.BookFullInfo;
import com.springproject.core.model.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.model.dto.BookDTO;
import com.springproject.core.model.dto.DetailedBookDTO;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.exceptions.SaveFileException;
import com.springproject.core.model.data.Constants;
import com.springproject.core.model.data.Elastic.search.BoolSearch;
import com.springproject.core.Services.search.SearchService;
import com.springproject.core.model.dto.Attachment;
import com.springproject.core.model.dto.KnnDTO;
import com.springproject.core.model.dto.WikidataSearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final BookFullInfoRepository bookFullInfoRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;


    @Operation(description = "В качестве \"additionalProp\" для запроса к содержанию книги указать \"chapters.content\"," +
            " для заголовка книги - \"title\", для авторов - \"authors\". Поля \"operator\" и \"fuzzy\" опциональные")
    @PostMapping("/search/advanced")
    public List<BookDTO> searchBookAdvanced(@RequestBody BoolSearch boolSearch) {
        return searchService.searchBookBool(boolSearch);
    }

    @PostMapping("/search/semantic")
    public List<BookDTO> knnSearch(@RequestBody KnnDTO knnDTO) {
        return searchService.searchBookKnn(knnDTO);
    }
    @PostMapping("/search/semantic/knn-expanded")
    public List<BookDTO> knnAndBoolSearch(@RequestBody KnnDTO query) {
        return searchService.searchBookKnnExpanded(query);
    }

    @PostMapping("/search/semantic/wikidata")
    public List<BookDTO> wikidataSearch(@RequestBody WikidataSearchDTO wikidataSearchDTO) {
        return searchService.wikidataSearch(wikidataSearchDTO);
    }

    @GetMapping("/{bookId}")
    public DetailedBookDTO getDetailedBook(@PathVariable String bookId) {
        BookFullInfo bookFullInfo = bookFullInfoRepository.findById(Long.parseLong(bookId)).orElseThrow(() -> new BookNotFoundException("Book not found"));
        Book bookReviews = bookRepository.findById(Long.parseLong(bookId)).orElseThrow(() -> new BookNotFoundException("Book not found"));

        DetailedBookDTO book = modelMapper.map(bookFullInfo, DetailedBookDTO.class);
        book.setReviewsCount(bookReviews.getCountReviews());
        book.setCoverImageUrl(constants.getImagePath() + bookFullInfo.getBook().getId());
        book.setTitle(bookFullInfo.getBook().getTitle());
        book.setAuthors(bookFullInfo.getBook().getAuthors());
        return book;
    }

    @Transactional
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает изображение обложки", content =
                    {@Content(mediaType = "image/jpeg")}),
            @ApiResponse(responseCode = "400", description = "В пути некорректный id"),
    })
    @GetMapping("/cover/{bookId}")
    public ResponseEntity<Resource> getImageBook(@PathVariable String bookId) {
        CoverImage image = attachmentService.getCover(Long.parseLong(bookId));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getMediaType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"cover" +
                                bookId + "." + image.getMediaType().substring(image.getMediaType().indexOf('/') + 1)
                                + "\"")
                .body(new ByteArrayResource(image.getCoverImage()));
    }

    //@Operation(description = "В качестве тела form-data отправить файл")
    //@PostMapping(value = "/load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/load")
    @PreAuthorize("hasRole('ADMIN')")
    public Long loadBook(@RequestParam("book") MultipartFile bookEpub) throws SaveFileException {
        if (bookEpub.isEmpty()) {
            throw new SaveFileException("File not found");
        }
        Long id = 0L;
        try {
            id = authService.getAuthInfo().getId();
        } catch (ClassCastException ignore) {
        }
        return attachmentService.saveBookEpub(bookEpub, id);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает книгу в epub", content =
                    {@Content(mediaType = "application/epub+zip")}),
            @ApiResponse(responseCode = "400", description = "В пути некорректный id"),
    })
    @GetMapping("/download/{bookId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long bookId) throws BookNotFoundException {
        Attachment attachment = attachmentService.getAttachment(bookId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(constants.type))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getName()
                                + "\"")
                .body(new ByteArrayResource(attachment.getBody()));
    }
}

