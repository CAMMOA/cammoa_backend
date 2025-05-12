package org.example.chat.controller;

import lombok.AllArgsConstructor;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {

    private ChatService chatService;















    //채팅방 나가기
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long roomId) {
        chatService.leaveChatRoom(roomId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.PARTICIPANT_LEAVED)
                        .build()
        );
    }
}
