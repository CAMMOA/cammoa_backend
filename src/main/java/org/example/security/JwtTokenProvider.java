package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.example.security.dto.JwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.example.security.constant.JwtTokenConstant.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        //Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORIZATION_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("Generated Token: " + accessToken);

        return JwtToken.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //Jwt 토큰을 복호화하여 사용자의 인증 정보 생성
    public Authentication getAuthentication(String accessToken){
        //Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORIZATION_KEY) == null){
            throw new RuntimeException("Invalid access token");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch  (SecurityException | MalformedJwtException e){
            log.warn("Invalid JWT token", e);
        } catch (ExpiredJwtException e){
            log.warn("Expired JWT token", e);
        } catch (UnsupportedJwtException e){
            log.warn("Unsupported JWT token", e);
        } catch (IllegalArgumentException e){
            log.warn("JWT claims string is empty", e);
        }
        return false;
    }

    //accessToken 복호화
    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
