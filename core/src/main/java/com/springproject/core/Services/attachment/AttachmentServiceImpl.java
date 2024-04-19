package com.springproject.core.Services.attachment;

import com.springproject.core.Services.NotificationService;
import com.springproject.core.Services.search.VectorService;
import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.exceptions.CoverNotFoundException;
import com.springproject.core.model.Entity.Book;
import com.springproject.core.model.Entity.BookFullInfo;
import com.springproject.core.model.Entity.CoverImage;
import com.springproject.core.Repository.BookFullInfoRepository;
import com.springproject.core.Repository.CoverImageRepository;
import com.springproject.core.exceptions.InvalidBookTypeException;
import com.springproject.core.exceptions.SaveFileException;
import com.springproject.core.model.data.Constants;
import com.springproject.core.model.data.Elastic.ElasticBook;
import com.springproject.core.Repository.BookRepository;
import com.springproject.core.Repository.ElasticBookRepository;
import com.springproject.core.model.dto.Attachment;
import com.springproject.core.model.data.ExtractBookInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService{
    private final Constants constants;
    private final BookRepository bookRepository;
    private final BookFullInfoRepository bookFullInfoRepository;
    private final ElasticBookRepository elasticBookRepository;
    private final CoverImageRepository coverImageRepository;
    private final ExtractEpubService extractEpubService;
    private final ModelMapper modelMapper;
    private final VectorService vectorService;
    private final NotificationService notificationService;
    public void saveBookEpub(MultipartFile bookEpub, String uniqueString) {
        if (!constants.type.equals(bookEpub.getContentType())) {
            throw new InvalidBookTypeException("Invalid type of the file");
        }
        String fileName = saveInExplorer(bookEpub, uniqueString);
        try (InputStream inputBook = bookEpub.getInputStream()) {
            ExtractBookInfo fullBook = extractEpubService.extractInfoFromEpub(inputBook);
            fullBook.setSize(bookEpub.getSize());
            Long bookId = saveInDB(fileName, fullBook);

            ElasticBook book = modelMapper.map(fullBook, ElasticBook.class);
            book.setId(bookId);
            book.getChapters().forEach(chapter -> chapter.setVector(vectorService.getVector(chapter.getContent())));
            elasticBookRepository.save(book);
            notificationService.sendNotification(book.getChapters());
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
        ExtractBookInfo fullBook = extractEpubService.extractInfoFromEpub(fileInputStreamForEpub);
        fullBook.setSize(size);
        Long bookId = saveInDB(fileName, fullBook);
        ElasticBook book = modelMapper.map(fullBook, ElasticBook.class);
        book.setId(bookId);
        book.getChapters().forEach(chapter -> chapter.setVector(vectorService.getVector(chapter.getContent())));
        elasticBookRepository.save(book);
    }


    public Attachment getAttachment(Long fileId) throws BookNotFoundException {
        Book book = bookRepository.findById(fileId).orElseThrow(() -> new BookNotFoundException("Invalid id"));

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

    @Override
    public CoverImage getCover(Long bookId) {
        CoverImage image;
        try {
            image = coverImageRepository.getReferenceById(bookId);
            if (image.getCoverImage().length < 6000) {
                image = getDefaultCover();
            }
        } catch (EntityNotFoundException ignored) {
            image = getDefaultCover();
        }
        return image;
    }
    private CoverImage getDefaultCover() {
        Random rand = new Random();
        int randomInt = rand.nextInt(1, 9);
        CoverImage image = new CoverImage();
        try (InputStream cover = new FileInputStream(constants.defaultImages +
                randomInt +".png")) {
            image.setCoverImage(cover.readAllBytes());
            image.setMediaType("image/png");
            return image;
        } catch (IOException ignored1) {
            throw new CoverNotFoundException("Cover not found");
        }
    }

}
