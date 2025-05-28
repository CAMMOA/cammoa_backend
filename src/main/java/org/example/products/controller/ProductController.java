package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.response.JoinChatRoomResponse;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.products.constant.SortTypeEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.request.ProductUpdateRequest;
import org.example.products.dto.response.ProductDetailResponse;
import org.example.products.dto.response.ProductListResponse;
import org.example.products.dto.response.ProductResponse;
import org.example.products.repository.entity.CategoryEnum;
import org.example.products.service.ProductService;
import org.example.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;
    //게시글 생성
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);
        ProductResponse response = productService.createProduct(request, userId);
        URI location = URI.create("/api/posts/" + response.getProductId());

        return ResponseEntity.created(location)
                .body(CommonResponseEntity.<ProductResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.RESOURCES_CREATED)
                        .build());
    }
    //게시글 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam(value = "category", required = false) String categoryStr) {
        CategoryEnum category = null;
        if (categoryStr != null && !categoryStr.isBlank()) {
            try {
                category = CategoryEnum.from(categoryStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        CommonResponseEntity.builder()
                                .response(ErrorResponseEnum.INVALID_REQUEST)
                                .build()
                );
            }
        }
        List<ProductListResponse> responseList = productService.getAllProductsByCategory(category);

        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductListResponse>>builder()
                        .data(responseList)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendedProducts() {
        List<ProductListResponse> products = productService.getRecommendedProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductListResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/closing-soon")
    public ResponseEntity<?> getClosingSoonProducts() {
        List<ProductListResponse> products = productService.getClosingSoonProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductListResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentlyPostedProducts() {
        List<ProductListResponse> products = productService.getRecentlyPostedProducts();
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductListResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }
    //게시글 상세페이지 조회
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
    //게시글 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "category", required = false) String category, @RequestParam(value = "sortTypeEnum", defaultValue = "DEADLINE") SortTypeEnum sortTypeEnum) {

        CategoryEnum categoryEnum = null;
        if (category != null && !category.isBlank()) {
            try {
                categoryEnum = CategoryEnum.valueOf(category);
            } catch (IllegalArgumentException e) {
                categoryEnum = null; // 잘못된 값이 들어오면 null 처리
            }
        }

        List<ProductResponse> products =  productService.searchProductsByKeywordAndCategory(keyword, categoryEnum, sortTypeEnum);

        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductResponse>>builder()
                        .data(products)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }
    //게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody @Valid ProductUpdateRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);
        ProductResponse response = productService.updateProduct(postId, request, userId);

        return ResponseEntity.ok(
                CommonResponseEntity.<ProductResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }
    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);
        productService.deleteProduct(postId, userId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.POST_DELETE_SUCCESS)
                        .build()
        );
    }

    //채팅방 참여
    @PostMapping("/{postId}/chat/join")
    public ResponseEntity<?> joinChatRoom(@PathVariable Long postId) {

        JoinChatRoomResponse response = productService.joinChatRoom(postId);

        return ResponseEntity.ok(
                CommonResponseEntity.<JoinChatRoomResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.CHATROOM_JOIN_SUCCESS)
                        .build()
        );
    }
}
