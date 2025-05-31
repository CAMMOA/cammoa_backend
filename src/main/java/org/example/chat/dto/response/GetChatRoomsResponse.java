package org.example.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.products.dto.response.ProductSimpleResponse;

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
    private ProductSimpleResponse product;

    @Data
    @Builder
    public static class LastMessageDto {
        private String content;
        private LocalDateTime time;
    }
}
