package org.example.products.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
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
    @NotNull @Positive
    private int price;
    @NotNull @Future
    private LocalDateTime deadline;
    @NotNull @Positive
    private int numPeople;
    @NotBlank
    private String place;
    @NotBlank
    private String status;
    @NotNull @Positive
    private int maxParticipants;
}
