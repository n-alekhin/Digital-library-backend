package com.springproject.core.Services;

import com.springproject.core.Entity.Book;
import com.springproject.core.Entity.Constants;
import com.springproject.core.Entity.Elastic.ElasticBook;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Repository.ElasticBookRepository;
import com.springproject.core.dto.Attachment;
import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.util.IOUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final BookRepository bookRepository;
    private final ElasticBookRepository elasticBookRepository;
    private final EpubService epubService;


    public void saveBookEpub(MultipartFile bookEpub) throws Exception {
        if (!Constants.type.equals(bookEpub.getContentType())) {
            throw new Exception("Invalid type");
        }
        String fileName = saveInExplorer(bookEpub);
        try (InputStream inputBook = bookEpub.getInputStream()) {
            ElasticBook book = epubService.extractInfoFromEpub(inputBook);
            Book bookDB = saveInDB(fileName, book.getTitle());
            book.setId(bookDB.getId());
            double[] vector = new double[384];
            for (int i = 0; i < 384; i++)
                vector[i] = 1;
            book.setMyVector(vector);
            elasticBookRepository.save(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Book saveInDB(String fileName, String title) {
        Optional<Book> oldBook = bookRepository.findByFileName(fileName);
        if (oldBook.isPresent())
            return oldBook.get();
        Book bookDB = new Book();
        bookDB.setFileName(fileName);
        bookDB.setTitle(title);
        bookDB = bookRepository.save(bookDB);
        return bookDB;
    }
    private String saveInExplorer(MultipartFile bookEpub) throws Exception {
        String root = System.getProperty("user.dir") + "\\";
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(bookEpub.getOriginalFilename()));
        String path = root + Constants.storagePath + fileName;
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence "
                        + fileName);
            }
            bookEpub.transferTo(Paths.get(path));
        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
        return fileName;
    }
    public void saveBookForTesting(String fileName, InputStream fileInputStream, InputStream fileInputStreamForEpub) throws IOException {

        String root = System.getProperty("user.dir") + "\\";
        String path = root + Constants.storagePath + fileName;
        File file = new File(path);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ElasticBook book = epubService.extractInfoFromEpub(fileInputStreamForEpub);
        Book bookDB = saveInDB(fileName, book.getTitle());
        book.setId(bookDB.getId());
        double[] vector = new double[384];
        for (int i = 0; i < 384; i++)
            vector[i] = 1;
        book.setMyVector(vector);
        elasticBookRepository.save(book);
    }


    public Attachment getAttachment(Long fileId) throws Exception {
        Book book = bookRepository.findById(fileId).orElseThrow(() -> new Exception("Invalid id"));

        try(InputStream inputStream = Files.newInputStream(Paths.get(Constants.storagePath + book.getFileName()))) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            byte[] bytes = outputStream.toByteArray();
            return new Attachment(book.getFileName(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
