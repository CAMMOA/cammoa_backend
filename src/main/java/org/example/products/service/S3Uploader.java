package org.example.products.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Profile("!local") // 로컬 환경 제외, 배포 환경에서 사용
public class S3Uploader implements FileUploader {

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        // S3 업로드 로직 (임시 구현)
        return "https://s3-bucket-url/" + file.getOriginalFilename();
    }
}
