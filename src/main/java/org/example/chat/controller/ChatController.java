package org.example.chat.controller;

import lombok.AllArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {

    private ChatService chatService;

    //이전 메시지 조회
    @GetMapping("history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessageDto> chatMessageList = chatService.getChatHistory(roomId);

        return ResponseEntity.ok(
                CommonResponseEntity.<List<ChatMessageDto>>builder()
                        .data(chatMessageList)
                        .response(SuccessResponseEnum.RESOURCES_GET)
                        .build()
        );
    }

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
