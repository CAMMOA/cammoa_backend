package org.example.products.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Profile("local") // 로컬 환경에서만 동작
public class LocalUploader implements FileUploader {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + File.separator + fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + fileName;
    }

    @Override
    public void deleteFile(String imageUrl) {
        // 로컬 환경에서는 파일명만 추출하여 삭제
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        File file = new File(uploadDir, fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
