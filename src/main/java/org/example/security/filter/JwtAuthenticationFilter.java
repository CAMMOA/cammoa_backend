package org.example.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static org.example.security.constant.JwtTokenConstant.AUTH_ACCESS_HEADER;
import static org.example.security.constant.JwtTokenConstant.BEARER_PREFIX;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 로그인/회원가입 경로 필터링 제외
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/signup")) {
            chain.doFilter(request, response); // 수정: chain 변수명 일치
            return;
        }

        String token = resolveToken((HttpServletRequest) request);

        // validateToken으로 토큰 유효성 검사
        if(token != null && jwtTokenProvider.validateToken(token)){
            //Authentication 객체 가져와 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    //Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTH_ACCESS_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }
}
