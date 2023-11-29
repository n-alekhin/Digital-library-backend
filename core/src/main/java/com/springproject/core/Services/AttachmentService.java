package com.springproject.core.Services;

import com.springproject.core.Entity.Book;
import com.springproject.core.Entity.BookFullInfo;
import com.springproject.core.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.CoverImageRepository;
import com.springproject.core.exceptions.InvalidBookTypeException;
import com.springproject.core.exceptions.SaveFileException;
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
import java.util.List;
import java.util.Objects;

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
    private final VectorService vectorService;

    public void saveBookEpub(MultipartFile bookEpub, String uniqueString) {
        if (!constants.type.equals(bookEpub.getContentType())) {
            throw new InvalidBookTypeException("Invalid type of the file");
        }
        String fileName = saveInExplorer(bookEpub, uniqueString);
        try (InputStream inputBook = bookEpub.getInputStream()) {
            ExtractBookInfo fullBook = epubService.extractInfoFromEpub(inputBook);
            fullBook.setSize(bookEpub.getSize());
            Long bookId = saveInDB(fileName, fullBook);

            ElasticBook book = modelMapper.map(fullBook, ElasticBook.class);
            book.setId(bookId);
            book.getChapters().forEach(chapter -> {
                double[] vector = listToArray(vectorService.getVector(chapter.getContent()));
                chapter.setVector(vector);
            });

            elasticBookRepository.save(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long saveInDB(String fileName, ExtractBookInfo fullBook) {
        Book bookDB = bookRepository.findByFileName(fileName).orElse(new Book());
        bookDB.setTitle(fullBook.getTitle());
        bookDB.setAuthors(fullBook.getAuthors().stream().reduce((acc, s) -> acc.concat(", " + s)).orElse(null));
        bookDB.setFileName(fileName);
        bookDB = bookRepository.save(bookDB);

        BookFullInfo bookFullInfo = bookFullInfoRepository.findById(bookDB.getId())
                .orElse(new BookFullInfo(bookDB));
        modelMapper.map(fullBook, bookFullInfo);
        bookFullInfoRepository.save(bookFullInfo);

        CoverImage image = coverImageRepository.findById(bookDB.getId())
                .orElse(new CoverImage(bookDB));
        modelMapper.map(fullBook, image);
        if (image.getMediaType() == null || image.getMediaType().isEmpty()) {
            image.setMediaType(constants.defaultTypeOfImage);
        }
        coverImageRepository.save(image);
        return bookDB.getId();
    }
    private String saveInExplorer(MultipartFile bookEpub, String uniqueString) {
        String root = System.getProperty("user.dir") + "\\";
        String fileName = uniqueString + "-" + StringUtils.cleanPath(Objects.requireNonNull(bookEpub.getOriginalFilename()));
        String path = root + constants.storagePath + fileName;
        try {
            if (fileName.contains("..")) {
                throw new SaveFileException("Filename contains invalid path sequence "
                        + fileName);
            }
            bookEpub.transferTo(Paths.get(path));
        } catch (Exception e) {
            throw new SaveFileException("Could not save File: " + fileName);
        }
        return fileName;
    }
    public void saveBookForTesting(String fileName, InputStream fileInputStream, InputStream fileInputStreamForEpub, long size, String uniqueString) {
        fileName = uniqueString + "-" + fileName;
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
        book.getChapters().forEach(chapter -> chapter.setVector(vector));
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

    private static double[] listToArray(List<Double> doubleList) {
        double[] doubleArray = new double[doubleList.size()];
        for (int i = 0; i < doubleList.size(); i++) {
            doubleArray[i] = doubleList.get(i);
        }
        return doubleArray;
    }
}
