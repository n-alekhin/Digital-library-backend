package com.springproject.core.Services.attachment;

import com.springproject.core.exceptions.BookNotFoundException;
import com.springproject.core.model.dto.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment getAttachment(Long fileId) throws BookNotFoundException;
    void saveBookEpub(MultipartFile bookEpub, String uniqueString);
}
