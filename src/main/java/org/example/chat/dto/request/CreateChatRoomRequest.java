package org.example.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateChatRoomRequest {

    @NotBlank
    private Long productId;

    @NotBlank
    private String chatRoomName;
}
