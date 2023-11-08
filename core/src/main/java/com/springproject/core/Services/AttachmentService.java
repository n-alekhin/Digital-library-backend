package com.springproject.core.Services;

import com.springproject.core.Entity.Book;
import com.springproject.core.Entity.BookFullInfo;
import com.springproject.core.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.CoverImageRepository;
import com.springproject.core.model.Constants;
import com.springproject.core.model.Elastic.ElasticBook;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Repository.ElasticBookRepository;
import com.springproject.core.dto.Attachment;
import com.springproject.core.model.ExtractBookInfo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final Constants constants;
    private final BookRepository bookRepository;
    private final BookFullInfoRepository bookFullInfoRepository;
    private final ElasticBookRepository elasticBookRepository;
    private final CoverImageRepository coverImageRepository;
    private final EpubService epubService;
    private final ModelMapper modelMapper;


    public void saveBookEpub(MultipartFile bookEpub) throws Exception {
        if (!constants.type.equals(bookEpub.getContentType())) {
            throw new Exception("Invalid type");
        }
        String fileName = saveInExplorer(bookEpub);
        try (InputStream inputBook = bookEpub.getInputStream()) {
            ExtractBookInfo fullBook = epubService.extractInfoFromEpub(inputBook);
            fullBook.setSize(bookEpub.getSize());
            Long bookId = saveInDB(fileName, fullBook);
            ElasticBook book = modelMapper.map(fullBook, ElasticBook.class);
            book.setId(bookId);
            double[] vector = new double[384];
            for (int i = 0; i < 384; i++)
                vector[i] = 1;
            book.setMyVector(vector);
            elasticBookRepository.save(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long saveInDB(String fileName, ExtractBookInfo fullBook) {
        Optional<Book> oldBook = bookRepository.findByFileName(fileName);
        if (oldBook.isPresent())
            return oldBook.get().getId();
        Book bookDB = modelMapper.map(fullBook, Book.class);
        bookDB.setFileName(fileName);
        bookDB = bookRepository.save(bookDB);

        BookFullInfo bookFullInfo = modelMapper.map(fullBook, BookFullInfo.class);
        bookFullInfo.setBook(bookDB);
        bookFullInfoRepository.save(bookFullInfo);

        CoverImage image = modelMapper.map(fullBook, CoverImage.class);
        image.setBook(bookDB);
        if (image.getMediaType() == null || image.getMediaType().isEmpty()) {
            image.setMediaType(constants.defaultTypeOfImage);
        }
        coverImageRepository.save(image);
        return bookDB.getId();
    }
    private String saveInExplorer(MultipartFile bookEpub) throws Exception {
        String root = System.getProperty("user.dir") + "\\";
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(bookEpub.getOriginalFilename()));
        String path = root + constants.storagePath + fileName;
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
    public void saveBookForTesting(String fileName, InputStream fileInputStream, InputStream fileInputStreamForEpub, long size) {

        String root = System.getProperty("user.dir") + "\\";
        String path = root + constants.storagePath + fileName;
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
        ExtractBookInfo fullBook = epubService.extractInfoFromEpub(fileInputStreamForEpub);
        fullBook.setSize(size);
        Long bookId = saveInDB(fileName, fullBook);
        ElasticBook book = modelMapper.map(fullBook, ElasticBook.class);
        book.setId(bookId);
        double[] vector = new double[384];
        for (int i = 0; i < 384; i++)
            vector[i] = 1;
        book.setMyVector(vector);
        elasticBookRepository.save(book);
    }


    public Attachment getAttachment(Long fileId) throws Exception {
        Book book = bookRepository.findById(fileId).orElseThrow(() -> new Exception("Invalid id"));

        try(InputStream inputStream = Files.newInputStream(Paths.get(constants.storagePath + book.getFileName()))) {
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
