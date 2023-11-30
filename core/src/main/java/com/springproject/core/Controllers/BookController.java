package com.springproject.core.Controllers;

import com.springproject.core.Entity.BookFullInfo;
import com.springproject.core.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.CoverImageRepository;
import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.dto.BookDTO;
import com.springproject.core.dto.DetailedBookDTO;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.exceptions.SaveFileException;
import com.springproject.core.model.Constants;
import com.springproject.core.model.Elastic.search.BoolSearch;
import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.SearchService;
import com.springproject.core.dto.Attachment;
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
    private final AuthService authService;


    @Operation(description = "В качестве \"additionalProp\" для запроса к содержанию книги указать \"chapters.content\"," +
            " для заголовка книги - \"title\", для авторов - \"authors\". Поля \"operator\" и \"fuzzy\" опциональные")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает изображение обложки", content =
                    { @Content(mediaType = "image/jpeg")}),
            @ApiResponse(responseCode = "400", description = "В пути некорректный id"),
    })
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
    @Operation(description = "В качестве тела form-data отправить файл")
    @PostMapping(value = "/load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadBook(@RequestParam("book") MultipartFile bookEpub) throws SaveFileException {
        if (bookEpub.isEmpty()) {
            throw new SaveFileException("File not found");
        }
        String id = "0";
        try {
            id = authService.getAuthInfo().getId().toString();
        } catch (ClassCastException ignore) {}
        attachmentService.saveBookEpub(bookEpub, id);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает книгу в epub", content =
                    { @Content(mediaType = "application/epub+zip")}),
            @ApiResponse(responseCode = "400", description = "В пути некорректный id"),
    })
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

