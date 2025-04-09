package org.example.users.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.email.service.EmailService;
import org.example.exception.impl.AuthException;
import org.example.redis.RedisService;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.AuthenticationException;

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

    public SendEmailResponse sendAuthcode(String email) throws MessagingException {
        try{
            String authCode = emailService.sendSimpleMessage(email);

            //Redis에 인증 코드 저장
            redisService.setCode(email, authCode);

            return SendEmailResponse.builder()
                    .authCode(authCode)
                    .build();

        } catch (MessagingException e) {
            throw new AuthException(ErrorResponseEnum.EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new AuthException(ErrorResponseEnum.AUTH_CODE_NOT_FOUND);
        }

    }

    public void validationAuthCode(@RequestBody ValidateEmailRequest request) {

        String savedCode = redisService.getCode(request.getEmail());

        if (StringUtils.isEmpty(savedCode)) {
            throw new AuthException(ErrorResponseEnum.AUTH_CODE_NOT_FOUND);
        }

        // 2. 코드 불일치 시
        if (!savedCode.equals(request.getAuthCode())) {
            throw new AuthException(ErrorResponseEnum.AUTH_CODE_MISMATCH);
        }
    }
}
