package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {
    private Long productId;
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
    private Long chatRoomId;
    private String chatRoomName;

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }
}
