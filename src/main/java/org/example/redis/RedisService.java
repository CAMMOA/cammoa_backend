package org.example.redis;

import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.impl.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    //데이터 조회
    public String getData(String key){
        return redisTemplate.opsForValue().get(key);
    }

    //키 존재 여부 확인
    public boolean existData(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    //데이터 저장 + 만료 시간 설정
    public void setDataExpire(String key, String value, long duration){
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }

    public void deleteData(String email) {
        redisTemplate.delete(email);
    }

    // K: 이메일, V: 인증코드
    public void setCode(String email, String authCode){
        long seconds = authCodeExpirationMillis / 1000;
        // 유효 시간은 300, 단위는 초로 설정
        setDataExpire(email, authCode, seconds);
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

    public void setVerified(String email) {
        redisTemplate.opsForValue().set("verified:" + email, "true", Duration.ofSeconds(300));
    }

    public boolean isVerified(String email) {
        String value = redisTemplate.opsForValue().get("verified:" + email);
        return "true".equals(value);
    }

    public void setBlackList(String token, String value, long expirationMills){
        redisTemplate.opsForValue().set("blacklist:" + token, value, expirationMills);
    }

    public boolean isBlackList(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}
