package org.example.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.service.ChatService;
import org.example.chat.service.RedisPubSubService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatService chatService;
    private final RedisPubSubService redisPubSubService;

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행 시 MessageMapping 수신
    // @DestinationVariable: @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto request) throws JsonProcessingException {
        System.out.println("======= 컴트롤러 진입 ======");
        System.out.println(request);
        chatService.saveMessage(roomId, request);
        request.setRoomId(roomId);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(request);

        redisPubSubService.publish("chat", message);
    }
}
