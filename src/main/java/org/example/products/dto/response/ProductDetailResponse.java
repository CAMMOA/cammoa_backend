package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductDetailResponse {
    private Long productId;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private Integer price;
    private LocalDateTime deadline;
    private String place;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username; // 작성자 이름
    private List<ProductSimpleResponse> relatedPosts;
}
