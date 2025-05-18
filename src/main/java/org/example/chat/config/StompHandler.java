package org.example.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

import java.security.Key;

@Component
public class StompHandler implements ChannelInterceptor {

    private final Key key;
    private final ChatService chatService;

    public StompHandler(ChatService chatService, @Value("${jwt.secret}") String secretKey) {
        this.chatService = chatService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("STOMP Command: " + accessor.getCommand());
        System.out.println("Headers: " + accessor.toNativeHeaderMap());

        if(StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("connect 요청 시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ChatException(ErrorResponseEnum.INVALID_TOKEN);
            }
            String token = bearerToken.substring(7);

            //토큰 검증
            Jwts.parserBuilder()
                    .setSigningKey(key)
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
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            System.out.println("검증 대상 이메일: " + email);
            String destination = accessor.getDestination();
            String roomId = null;
            if (destination != null) {
                String[] split = destination.split("/");
                if (split.length >= 3) {
                    roomId = split[2];
                } else if (split.length == 2) {
                    roomId = split[1];
                }
            }
            if (roomId == null) {
                throw new ChatException(ErrorResponseEnum.CHATROOM_NOT_FOUND);
            }
            if(!chatService.isRoomParticipant(email, Long.parseLong(roomId))){
                throw new ChatException(ErrorResponseEnum.PARTICIPANT_NOT_FOUND);
            }
        }

        return message;
    }
}
