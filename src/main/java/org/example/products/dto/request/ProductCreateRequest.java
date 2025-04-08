package org.example.products.dto.request;

import lombok.Getter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
public class ProductCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String category;
    @NotBlank
    private String description;
    @NotBlank
    private String image;
    @NotNull
    private int price;
    @NotNull
    private LocalDateTime deadline;
    @NotNull
    private int numPeople;
    @NotBlank
    private String place;
    @NotBlank
    private String status;
    @NotNull
    private int maxParticipants;
}
