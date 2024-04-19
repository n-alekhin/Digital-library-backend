package com.springproject.core.Services;

import com.springproject.core.model.data.Elastic.ElasticPartChapter;

import java.util.List;

public interface NotificationService {
    void sendNotification(List<ElasticPartChapter> chapters);
}
