package org.example.security.constant;

public class JwtTokenConstant {
    public static final String AUTH_ACCESS_HEADER = "Authorization";
    public static final String AUTH_REFRESH_HEADER = "RefreshToken";

    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";

    // Token 유효기간
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L; // 1시간
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 1주
}
