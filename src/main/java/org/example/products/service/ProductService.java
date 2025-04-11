package org.example.products.service;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.CustomException;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.response.ProductDetailResponse;
import org.example.products.dto.response.ProductResponse;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.ProductRepository;
import org.example.exception.impl.InvalidRequestException;
import org.example.users.repository.UserJpaRepository;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;

    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("마감일은 현재 시각보다 이후여야 합니다.");
        }

        if (request.getMaxParticipants() < request.getNumPeople()) {
            throw new InvalidRequestException("최대 인원은 현재 참여 인원보다 커야 합니다.");
        }

        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.USER_NOT_FOUND));


        ProductEntity product = ProductEntity.builder()
                .user(user)
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .image(request.getImage())
                .price(request.getPrice())
                .deadline(request.getDeadline())
                .numPeople(request.getNumPeople())
                .place(request.getPlace())
                .status(request.getStatus())
                .maxParticipants(request.getMaxParticipants())
                .build();

        ProductEntity saved = productRepository.save(product);

        return toProductResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    // 오늘의 공동구매 추천 (랜덤덤 8개)
    public List<ProductResponse> getRecommendedProducts() {
        List<ProductEntity> products = productRepository.findRandomRecommendedProducts();
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    // 곧 마감되는 공구 (마감일 빠른 순 8개)
    public List<ProductResponse> getClosingSoonProducts() {
        List<ProductEntity> products = productRepository.findClosingSoonProducts();
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    // 방금 올라온 공구 (생성일 최신순 8개)
    public List<ProductResponse> getRecentlyPostedProducts() {
        List<ProductEntity> products = productRepository.findRecentProducts(LocalDateTime.now().minusHours(24));
        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }


    private ProductResponse toProductResponse(ProductEntity product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .category(product.getCategory())
                .description(product.getDescription())
                .image(product.getImage())
                .price(product.getPrice())
                .deadline(product.getDeadline())
                .numPeople(product.getNumPeople())
                .place(product.getPlace())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .status(product.getStatus())
                .currentParticipants(product.getCurrentParticipants())
                .maxParticipants(product.getMaxParticipants())
                .build();
    }

    public ProductDetailResponse getProductDetail(Long productId) {
        ProductEntity product = productRepository.findByIdWithUser(productId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.POST_NOT_FOUND)); // 예외처리

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .category(product.getCategory())
                .image(product.getImage())
                .price(product.getPrice())
                .deadline(product.getDeadline())
                .place(product.getPlace())
                .currentParticipants(product.getCurrentParticipants())
                .maxParticipants(product.getMaxParticipants())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .username(product.getUser().getUsername())  // 작성자 이름
                .build();
    }

}



