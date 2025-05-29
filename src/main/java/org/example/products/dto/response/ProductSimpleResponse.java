package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductSimpleResponse {
    private Long productId;
    private String imageUrl;
    private String title;
    private int currentParticipants;
    private int maxParticipants;
    private int price;
    private LocalDateTime deadline;
}
