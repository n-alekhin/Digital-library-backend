package com.springproject.core.Controllers;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.springproject.core.Entity.Constants;
import com.springproject.core.Entity.Elastic.BoolSearch;
import com.springproject.core.Entity.Elastic.ElasticBook;
import com.springproject.core.Entity.Elastic.ElasticMathQuery;
import com.springproject.core.Services.AttachmentService;
import com.springproject.core.Services.SearchService;
import com.springproject.core.dto.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final ElasticsearchOperations operations;
    private final AttachmentService attachmentService;
    private final SearchService searchService;

    @PostMapping("/delete-index")
    public Boolean deleteIndex() {
        return operations.indexOps(ElasticBook.class).delete();
    }

    @GetMapping("/search/authors")
    public List<ElasticBook> searchBook(@RequestParam String query) {
        Map<String, ElasticMathQuery> must = new HashMap<>();
        must.put("authors", new ElasticMathQuery(query, Operator.Or));
        return searchService.searchBookBool(BoolSearch.builder().must(must).build());
    }
    @GetMapping("/search/chapters")
    public List<ElasticBook> searchBookByChapters(@RequestParam String query) {
        Map<String, ElasticMathQuery> must = new HashMap<>();
        must.put("chapters.content", new ElasticMathQuery(query, Operator.And));
        return searchService.searchBookBool(BoolSearch.builder().must(must).build());
    }
    @GetMapping("/search/title")
    public List<ElasticBook> searchBookByTitle(@RequestParam String query) {
        Map<String, ElasticMathQuery> must = new HashMap<>();
        must.put("title", new ElasticMathQuery(query, Operator.Or));
        return searchService.searchBookBool(BoolSearch.builder().must(must).build());
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
                .contentType(MediaType.parseMediaType(Constants.type))
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
