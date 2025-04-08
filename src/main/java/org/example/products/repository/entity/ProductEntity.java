package org.example.products.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long userId;

    private String title;

    private String category;

    private String description;

    private String image;

    private int price;

    private LocalDateTime deadline;

    private int numPeople;

    private String place;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    private int currentParticipants;

    private int maxParticipants;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currentParticipants = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}