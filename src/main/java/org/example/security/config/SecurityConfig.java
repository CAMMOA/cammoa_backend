package org.example.security.config;

import lombok.RequiredArgsConstructor;
import org.example.security.JwtTokenProvider;
import org.example.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable) //csrf 비활성화
                .csrf(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화
                //JWT를 사용하기 때문에 세션 사용 X
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/connect").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll() //게시글 목록 조회, 상세페이지, 검색 기능 로그인 없이 접근 가능하도록 수정
                        .anyRequest().authenticated())
                //JWT 인증을 위하여 직접 구현한 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //연동을 위한 코드
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트와 연동할 주소
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*")); //모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); //모든 헤더값 허용
        configuration.setAllowCredentials(true); //자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //모든 url 패턴에 대해 cors 허용 설정
        return source;
    }
}