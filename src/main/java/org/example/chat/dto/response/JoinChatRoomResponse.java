package org.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.products.dto.response.ProductResponse;
import org.example.products.dto.response.ProductSimpleResponse;

@Getter
@Builder
public class JoinChatRoomResponse {
    private Long roomId;
    private String roomName;
    private ProductSimpleResponse product;
}
