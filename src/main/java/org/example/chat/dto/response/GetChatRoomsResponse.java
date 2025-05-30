package org.example.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetChatRoomsResponse {
    private Long roomId;
    private String roomName;
    private Long unreadMessageCount;
    private LastMessageDto lastMessage;

    @Data
    @Builder
    public static class LastMessageDto {
        private String content;
        private LocalDateTime time;
    }
}
