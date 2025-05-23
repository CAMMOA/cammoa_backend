package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.products.repository.entity.ProductEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductListResponse {
    private Long id;
    private String title;
    private int price;
    private String imageUrl;
    private LocalDateTime deadline;

    public static ProductListResponse from(ProductEntity product) {
        return ProductListResponse.builder()
                .id(product.getProductId())
                .title(product.getTitle())
                .price(product.getPrice())
                .imageUrl(product.getImage())
                .deadline(product.getDeadline())
                .build();
    }
}
