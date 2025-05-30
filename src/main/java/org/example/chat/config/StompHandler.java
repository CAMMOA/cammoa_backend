package org.example.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.ChatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;

    private final ChatService chatService;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("connect 요청 시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
            }

            String token = bearerToken.substring(7);

            //토큰 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료");
        }

        if(StompCommand.SUBSCRIBE == accessor.getCommand()) {
            System.out.println("subscribe 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
            }
            String token = bearerToken.substring(7);

            //토큰 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];

            if(!chatService.isRoomParticipant(email, Long.parseLong(roomId))){
                throw new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND);
            }
        }

        return message;
    }
}
