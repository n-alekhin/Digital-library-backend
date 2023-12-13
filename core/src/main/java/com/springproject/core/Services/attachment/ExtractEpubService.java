package com.springproject.core.Services.attachment;

import com.springproject.core.model.data.ExtractBookInfo;

import java.io.InputStream;

public interface ExtractEpubService {
    ExtractBookInfo extractInfoFromEpub(InputStream epubStream);
}
