package org.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinChatRoomResponse {
    private Long roomId;
    private String roomName;
}
