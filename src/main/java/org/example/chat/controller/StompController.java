package org.example.chat.controller;

import lombok.AllArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행 시 MessageMapping 수신
    // @DestinationVariable: @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto request) {
        System.out.println("======= 컴트롤러 진입 ======");
        System.out.println(request);
        chatService.saveMessage(roomId, request);
        messagingTemplate.convertAndSend("/topic/"+roomId, request);
    }
}
