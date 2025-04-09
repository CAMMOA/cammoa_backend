package org.example.users.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.service.EmailService;
import org.example.exception.impl.AuthException;
import org.example.redis.RedisService;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;

    @Override
    @Transactional
    public UserResponse signup(@Valid @RequestBody UserCreateRequest request) {
        try {

            UserEntity userEntity = UserEntity.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .email(request.getEmail())
                    .build();

            UserEntity savedUser = userRepository.save(userEntity);
            return UserResponse.from(savedUser);

        } catch (Exception e) {
            throw new AuthException(ErrorResponseEnum.RESPONSE_NOT_VALID);
        }
    }

    public boolean sendAuthcode(String email) throws MessagingException {
        String authCode = emailService.sendSimpleMessage(email).getAuthCode();
        redisService.setCode(email, authCode);
        return true;
    }
}
