package org.example.products.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.products.service.FileUploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class ProductImageController {

    private final FileUploader fileUploadService;

    @PostMapping("/{productId}/images")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> images) throws IOException {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String url = fileUploadService.saveFile(image);
            imageUrls.add(url);
        }

        return ResponseEntity.ok(
                CommonResponseEntity.<List<String>>builder()
                        .data(imageUrls)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }
}
