package org.example.chat.controller;

import org.example.chat.dto.ChatMessageDto;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
