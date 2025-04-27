package org.example.products.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;
import org.example.products.repository.entity.CategoryEnum;

import java.time.LocalDateTime;


@Getter
public class ProductUpdateRequest {

    private String title;
    private CategoryEnum category;
    private String description;
    @Positive
    private Integer price;
    @Future
    private LocalDateTime deadline;
    private String place;
    private String image;

}
