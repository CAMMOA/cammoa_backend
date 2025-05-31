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
import org.example.security.JwtTokenProvider;
import org.example.users.repository.UserRepository;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

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
            @RequestBody ImageDeleteRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 1. 토큰 파싱 (Bearer 제거)
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : null;

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new CustomException(ErrorResponseEnum.INVALID_TOKEN);
        }

        // 2. 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserId(token);

        // 3. 게시글 조회
        ProductEntity product = productRepository.findByIdWithUserAndNotDeleted(postId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.POST_NOT_FOUND));

        // 4. 작성자 검증
        if (!product.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }

        // 5. 이미지 조회 및 삭제
        ProductImageEntity image = productImageRepository
                .findByProductAndImageUrl(product, request.getImageUrl())
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.IMAGE_NOT_FOUND));

        fileUploadService.deleteFile(request.getImageUrl());
        productImageRepository.delete(image);

        // 6. 대표 이미지였다면 null 처리
        if (request.getImageUrl().equals(product.getImage())) {
            List<ProductImageEntity> remainingImages =
                    productImageRepository.findAllByProduct(product);

            remainingImages.remove(image); // 현재 삭제 대상 제거

            if (!remainingImages.isEmpty()) {
                product.setImage(remainingImages.get(0).getImageUrl()); // 다른 이미지로 대체
            } else {
                product.setImage(null); // 아예 없으면 null
            }

            productRepository.save(product);
        }

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }


}
