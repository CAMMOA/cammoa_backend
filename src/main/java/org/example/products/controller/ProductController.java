package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.response.ProductDetailResponse;
import org.example.products.dto.response.ProductResponse;
import org.example.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responseList = productService.getAllProducts();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<ProductResponse>> getRecommendedProducts() {
        List<ProductResponse> products = productService.getRecommendedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/closing-soon")
    public ResponseEntity<List<ProductResponse>> getClosingSoonProducts() {
        List<ProductResponse> products = productService.getClosingSoonProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProductResponse>> getRecentlyPostedProducts() {
        List<ProductResponse> products = productService.getRecentlyPostedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable("postId") Long postId) {
        ProductDetailResponse response = productService.getProductDetail(postId);
        return ResponseEntity.ok(CommonResponseEntity.success(SuccessResponseEnum.OK, response));
    }
}
