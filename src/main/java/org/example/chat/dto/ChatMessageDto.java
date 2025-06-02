package org.example.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long roomId;
    private String message;
    private String senderNickname;
    private String senderEmail;
    private Long unreadMessageCount;
    @CreationTimestamp
    private LocalDateTime createdTime;
}
