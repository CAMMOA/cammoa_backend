package org.example.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.ChatMessageDto;
import org.example.chat.dto.request.SendMessageRequest;
import org.example.chat.service.ChatService;
import org.example.chat.service.RedisPubSubService;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ChatException;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatService chatService;
    private final RedisPubSubService redisPubSubService;
    private final UserRepository userRepository;

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행 시 MessageMapping 수신
    // @DestinationVariable: @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request, Principal principal) throws JsonProcessingException {
        System.out.println("======= 컨트롤러 진입 ======");
        System.out.println(request);
        String email = (principal != null) ? principal.getName() : null;

        if (email == null) {
            throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
        }

        UserEntity sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .roomId(roomId)
                .message(request.getMessage())
                .senderEmail(email)
                .senderNickname(sender.getNickname())
                .build();

        chatService.saveMessage(roomId, chatMessageDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(request);

        redisPubSubService.publish("chat", message);
    }
}
