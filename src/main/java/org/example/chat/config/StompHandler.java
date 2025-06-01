package org.example.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ChatException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                // JwtHandshakeInterceptor에서 세션에 userEmail 저장했으므로 가져오기
                String email = (String) accessor.getSessionAttributes().get("userEmail");
                if (email == null) {
                    throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
                }
                // 인증 정보 SecurityContext에 등록 (선택)
                accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
                log.info("CONNECT 성공 - 사용자: {}", email);
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String email = (String) accessor.getSessionAttributes().get("userEmail");
                if (email == null) {
                    throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
                }

                String destination = accessor.getDestination();
                if (destination == null || !destination.startsWith("/topic/")) {
                    throw new ChatException(ErrorResponseEnum.INVALID_DESTINATION);
                }

                String roomId = destination.split("/")[2];
                if (!chatService.isRoomParticipant(email, Long.parseLong(roomId))) {
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