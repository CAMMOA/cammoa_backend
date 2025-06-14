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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("STOMP 처리 - sessionId: {}", accessor.getSessionId());
        log.info("STOMP 처리 - 세션 attributes: {}", accessor.getSessionAttributes());

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                log.warn("세션 Attribute에서 복원 시도");
                authentication = (Authentication) accessor.getSessionAttributes().get("authentication");
                if(authentication != null) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    log.info("세션 Attribute에서 인증 정보 복원 성공: {}", authentication.getName());
                } else {
                    log.error("세션 Attribute에도 인증 정보 없음");
                }
            }
            if (authentication == null){
                log.error("STOMP 처리 실패 - 인증 정보 없음");
                throw new IllegalArgumentException("Authentication required");
            }

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                accessor.setUser(authentication);
                accessor.getSessionAttributes().put("user", authentication);
                log.info("CONNECT 성공 - Principal 설정 완료: {}", authentication.getName());
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String email = authentication.getName();
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

                accessor.setUser(authentication);
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