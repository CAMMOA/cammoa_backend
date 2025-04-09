package org.example.redis;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    // K: 이메일, V: 인증코드
    public void setCode(String email, String authCode){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        // 유효 시간은 300, 단위는 초로 설정
        valueOperations.set(email, authCode, 300, TimeUnit.SECONDS);
    }

    // 이메일의 인증 코드를 반환
    public String getCode(String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String authCode = valueOperations.get(email);
        if (authCode == null) {
            throw new AuthException(ErrorResponseEnum.RESPONSE_NOT_VALID);
        }
        return authCode;
    }
}
