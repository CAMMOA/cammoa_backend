package org.example.products.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomInfoResponse {
    private Long chatRoomId;
    private String chatRoomName;
}
