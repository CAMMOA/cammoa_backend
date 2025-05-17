package org.example.products.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import java.time.LocalDateTime;

import org.example.products.repository.entity.CategoryEnum;

@Getter
public class ProductCreateRequest {
    @Size(max = 40)
    @NotBlank
    private String title;
    @NotNull
    private CategoryEnum category;
    @Size(max = 1500)
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
