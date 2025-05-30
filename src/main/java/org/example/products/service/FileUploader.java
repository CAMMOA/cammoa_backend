package org.example.products.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploader {
    String saveFile(MultipartFile file) throws IOException;
    void deleteFile(String imageUrl);
}
