package org.example.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateChatRoomResponse {

    private Long chatRoomId;
    private String chatRoomName;

}
