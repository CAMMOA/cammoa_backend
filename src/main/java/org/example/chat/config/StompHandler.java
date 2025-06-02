package org.example.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ChatException;
import org.example.security.JwtHandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        log.info("STOMP 처리 - sessionId: {}", accessor.getSessionId());
        log.info("STOMP 처리 - 세션 attributes: {}", sessionAttributes);

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String email = (String) sessionAttributes.get("userEmail");
                if (email == null) {
                    log.warn("STOMP CONNECT - userEmail이 세션에 존재하지 않음");
                    throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
                }
                // 인증 정보 SecurityContext에 등록 (선택)
                accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
                log.info("CONNECT 성공 - 사용자: {}", email);
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String email = (String) accessor.getSessionAttributes().get("userEmail");
                if (email == null) {
                    log.warn("STOMP SUBSCRIBE - userEmail이 세션에 존재하지 않음");
                    throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
                }

                String destination = accessor.getDestination();
                if (destination == null || !destination.startsWith("/topic/")) {
                    log.warn("STOMP SUBSCRIBE - 잘못된 destination: {}", destination);
                    throw new ChatException(ErrorResponseEnum.INVALID_DESTINATION);
                }

                String roomId = destination.split("/")[2];
                if (!chatService.isRoomParticipant(email, Long.parseLong(roomId))) {
                    log.warn("STOMP SUBSCRIBE - {} 사용자가 방 {}에 참여하지 않음", email, roomId);
                    throw new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND);
                }

                log.info("SUBSCRIBE 성공 - 사용자: {}, Room: {}", email, roomId);
            }

        } catch (ChatException e) {
            log.error("STOMP 처리 중 예외 발생: {}", e.getMessage());
            return buildErrorMessage(e.getMessage(), accessor);
        } catch (Exception e) {
            log.error("예상치 못한 예외 발생", e);
            return buildErrorMessage("Internal server error", accessor);
        }

        return message;
    }

    private Message<?> buildErrorMessage(String errorMessage, StompHeaderAccessor accessor) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setSessionId(accessor.getSessionId());
        errorAccessor.setMessage(errorMessage);
        return MessageBuilder.createMessage(new byte[0], errorAccessor.getMessageHeaders());
    }
}