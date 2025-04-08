package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.response.ProductResponse;
import org.example.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long mockUserId = 1L; // 추후 JWT 토큰에서 추출 예정
        ProductResponse response = productService.createProduct(request, mockUserId);

        return ResponseEntity.ok(
                CommonResponseEntity.<ProductResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.RESOURCES_CREATED)
                        .build()
        );
    }
}
