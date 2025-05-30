package org.example.products.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.exception.CustomException;
import org.example.products.dto.request.ImageDeleteRequest;
import org.example.products.repository.ProductImageRepository;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.entity.ProductImageEntity;
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
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @PostMapping("/{productId}/images")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> images) throws IOException {

        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(productId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.POST_NOT_FOUND));

        List<String> imageUrls = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String url = fileUploadService.saveFile(image);
            imageUrls.add(url);

            ProductImageEntity imageEntity = ProductImageEntity.builder()
                    .product(product)
                    .imageUrl(url)
                    .build();
            productImageRepository.save(imageEntity);

            if (i == 0) {
                product.setImage(url); // ← ProductEntity.image 필드에 대표 이미지 설정
                productRepository.save(product);
            }
        }

        return ResponseEntity.ok(
                CommonResponseEntity.<List<String>>builder()
                        .data(imageUrls)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @DeleteMapping("/{postId}/images")
    public ResponseEntity<?> deleteImage(
            @PathVariable Long postId,
            @RequestBody ImageDeleteRequest request
    ) {
        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(postId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.POST_NOT_FOUND));

        ProductImageEntity image = productImageRepository
                .findByProductAndImageUrl(product, request.getImageUrl())
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.IMAGE_NOT_FOUND));

        // 1. S3에서 삭제
        fileUploadService.deleteFile(request.getImageUrl());

        // 2. DB에서 삭제
        productImageRepository.delete(image);

        // 3. 대표 이미지였다면 product.image 필드도 null 처리
        if (request.getImageUrl().equals(product.getImage())) {
            product.setImage(null);
            productRepository.save(product);
        }

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

}
