package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.response.JoinChatRoomResponse;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.products.constant.SortTypeEnum;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.request.ProductUpdateRequest;
import org.example.products.dto.response.ProductDetailResponse;
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
    public ResponseEntity<?> searchProducts(@RequestParam("keyword") String keyword, @RequestParam(value = "category", required = false) String category, @RequestParam(value = "sortTypeEnum", defaultValue = "DEADLINE") SortTypeEnum sortTypeEnum) {

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
