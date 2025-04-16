package org.example.products.service;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.CustomException;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.request.ProductUpdateRequest;
import org.example.products.dto.response.ProductDetailResponse;
import org.example.products.dto.response.ProductResponse;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.ProductRepository;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorResponseEnum.INVALID_DEADLINE);
        }

        if (request.getMaxParticipants() < request.getNumPeople()) {
            throw new CustomException(ErrorResponseEnum.INVALID_MAX_PARTICIPANTS);

        }

        UserEntity user = userRepository.findById(userId)
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

    public List<ProductResponse> searchProductsByKeyword(String keyword) {
        List<ProductEntity> products = productRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        if (products.isEmpty()) {
            throw new CustomException(ErrorResponseEnum.POST_NOT_FOUND);
        }

        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public ProductResponse updateProduct(Long postId, ProductUpdateRequest request, Long userId) {
        ProductEntity product = productRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new CustomException(ErrorResponseEnum.POST_NOT_FOUND));

        if (!product.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }

        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDeadline() != null) product.setDeadline(request.getDeadline());
        if (request.getPlace() != null) product.setPlace(request.getPlace());
        if (request.getImage() != null) product.setImage(request.getImage());

        return toProductResponse(product);
    }


}



