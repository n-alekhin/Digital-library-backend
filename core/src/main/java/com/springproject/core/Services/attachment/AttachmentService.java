package com.springproject.core.Services.attachment;

import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.model.Entity.CoverImage;
import com.springproject.core.model.dto.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment getAttachment(Long fileId) throws BookNotFoundException;
    CoverImage getCover(Long bookId);
    Long saveBookEpub(MultipartFile bookEpub, String uniqueString);
}
