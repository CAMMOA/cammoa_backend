package org.example.products.dto.request;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ProductCreateRequest {
    private String title;
    private String category;
    private String description;
    private String image;
    private int price;
    private LocalDateTime deadline;
    private int numPeople;
    private String place;
    private String status;
    private int maxParticipants;
}
