package org.example.products.service;

import lombok.RequiredArgsConstructor;
import org.example.products.dto.request.ProductCreateRequest;
import org.example.products.dto.response.ProductResponse;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {
        ProductEntity product = ProductEntity.builder()
                .userId(userId)
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

        return ProductResponse.builder()
                .productId(saved.getProductId())
                .title(saved.getTitle())
                .category(saved.getCategory())
                .description(saved.getDescription())
                .image(saved.getImage())
                .price(saved.getPrice())
                .deadline(saved.getDeadline())
                .numPeople(saved.getNumPeople())
                .place(saved.getPlace())
                .createdAt(saved.getCreatedAt())
                .status(saved.getStatus())
                .currentParticipants(saved.getCurrentParticipants())
                .maxParticipants(saved.getMaxParticipants())
                .build();
    }
}