package org.example.chat.config;

import lombok.RequiredArgsConstructor;
import org.example.security.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //브로커가 메시지를 특정 룸에 전송
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /publish/1 형태로 메시지 발행헤야 함을 설정
        // /publish로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/publish");

        // /topic/1 형태로 메시지를 수신해야 함을 설정
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(10)
                .maxPoolSize(20)
                .queueCapacity(1000);
        registration.interceptors(stompHandler);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(10)
                .maxPoolSize(20);
    }
}
