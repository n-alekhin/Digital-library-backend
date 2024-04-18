package com.springproject.core.model.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public final String storagePath = "client\\book\\";
    public final String defaultImages = "client/images/defaultCover";
    public final String pythonUrl = "http://localhost:5000/";
    public static final String elastic = "localhost:9200";
    @Value("${server.port:8080}")
    private String port;
    public final String type = "application/epub+zip";
    public final String defaultTypeOfImage = "image/jpeg";
    public String getServer() {
        return "http://localhost:" + port;
    }
    public String getImagePath() {
        return getServer() + "/book/cover/";
    }
}
