package org.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API에 적합)
                .csrf(AbstractHttpConfigurer::disable)

                // 요청 인증 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()  // 회원가입 허용 (POST /api/users)
                        .requestMatchers(HttpMethod.GET, "/api/auth/signup/email/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup/email/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll() // 게시글 생성 허용
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/posts/**").permitAll() //게시글 수정 허용


                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                );

        return http.build();
    }
}
