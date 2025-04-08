package org.example.products.controller;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        Long mockUserId = 1L; // 추후 JWT 토큰에서 추출 예정
        ProductResponse response = productService.createProduct(request, mockUserId);
        return ResponseEntity.ok(response);
    }

}

