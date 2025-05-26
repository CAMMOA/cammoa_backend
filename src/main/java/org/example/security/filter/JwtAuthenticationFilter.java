package org.example.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.redis.RedisService;
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
    private final RedisService redisService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/signup")) {
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(httpRequest);

        if (token != null) {
            if (redisService.isBlackList(token) || !jwtTokenProvider.validateToken(token)) {
                // 여기서 직접 JSON 응답 작성
                writeErrorResponse(httpResponse, ErrorResponseEnum.INVALID_TOKEN);
                return;
            }

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

    private void writeErrorResponse(HttpServletResponse response, ErrorResponseEnum errorEnum) throws IOException {
        response.setStatus(errorEnum.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        CommonResponseEntity<?> commonResponse = CommonResponseEntity.builder()
                .response(errorEnum)
                .data(null)
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }
}
