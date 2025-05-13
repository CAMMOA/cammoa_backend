package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.products.repository.entity.ProductEntity;

@Getter
@Builder
public class ProductListResponse {
    private Long id;
    private String title;
    private int price;
    private String imageUrl;

    public static ProductListResponse from(ProductEntity product) {
        return ProductListResponse.builder()
                .id(product.getProductId())
                .title(product.getTitle())
                .price(product.getPrice())
                .imageUrl(product.getImage())
                .build();
    }
}
