package org.example.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ChatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class StompHandler implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(StompHandler.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private final ChatService chatService;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                handleConnect(accessor);
            } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscribe(accessor);
            }
        } catch (ChatException e) {
            logger.error("STOMP 처리 중 예외 발생: {}", e.getMessage());
            return buildErrorMessage(e.getMessage(), accessor);
        } catch (Exception e) {
            logger.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
            return buildErrorMessage("Internal server error", accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
        }

        String token = bearerToken.substring(7);
        Claims claims = validateToken(token);
        String email = claims.getSubject();

        // Principal 등록
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
        logger.info("CONNECT 성공 - 사용자: {}", email);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
        }

        String token = bearerToken.substring(7);
        Claims claims = validateToken(token);
        String email = claims.getSubject();

        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/topic/")) {
            throw new ChatException(ErrorResponseEnum.INVALID_DESTINATION);
        }

        String roomId = destination.split("/")[2];
        if (!chatService.isRoomParticipant(email, Long.parseLong(roomId))) {
            throw new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND);
        }

        logger.info("SUBSCRIBE 성공 - 사용자: {}, Room: {}", email, roomId);
    }

    private Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
        }
    }

    private Message<?> buildErrorMessage(String errorMessage, StompHeaderAccessor accessor) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setSessionId(accessor.getSessionId());
        errorAccessor.setMessage(errorMessage);
        return MessageBuilder.createMessage(new byte[0], errorAccessor.getMessageHeaders());
    }
}