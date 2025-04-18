package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.request.ProductUpdateRequest;
import org.example.products.dto.response.ProductDetailResponse;
import org.example.products.dto.response.ProductResponse;
import org.example.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        URI location = URI.create("/api/posts/" + response.getProductId());

        return ResponseEntity.created(location)
                .body(CommonResponseEntity.<ProductResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.RESOURCES_CREATED)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<ProductResponse> responseList = productService.getAllProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(responseList)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendedProducts() {
        List<ProductResponse> products = productService.getRecommendedProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/closing-soon")
    public ResponseEntity<?> getClosingSoonProducts() {
        List<ProductResponse> products = productService.getClosingSoonProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentlyPostedProducts() {
        List<ProductResponse> products = productService.getRecentlyPostedProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable("postId") Long postId) {
        ProductDetailResponse response = productService.getProductDetail(postId);
        return ResponseEntity.ok(
                CommonResponseEntity.<ProductDetailResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam("keyword") String keyword) {
        List<ProductResponse> products = productService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody ProductUpdateRequest request) {
        Long mockUserId = 1L; // 추후 로그인 연동 시 교체 예정

        ProductResponse response = productService.updateProduct(postId, request, mockUserId);

        return ResponseEntity.ok(
                CommonResponseEntity.<ProductResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

}
