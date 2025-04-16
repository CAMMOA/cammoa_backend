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
import org.example.exception.impl.ResourceException;
import org.example.redis.RedisService;
import org.example.security.JwtTokenProvider;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

import static org.example.security.constant.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse signup(@Valid @RequestBody UserCreateRequest request) {
        List<Enum> roles = new ArrayList<>();
        roles.add(ROLE_USER);

        //유저 중복 확인
        if(userRepository.existsByUsername(request.getUsername())){
            throw new ResourceException(ErrorResponseEnum.DUPLICATED_USERNAME);
        }
        //이메일 중복 확인
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceException(ErrorResponseEnum.DUPLICATED_EMAIL);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        try {
            UserEntity userEntity = UserEntity.builder()
                    .username(request.getUsername())
                    .password(encodedPassword)
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

    @Override
    @Transactional
    public JwtToken login(String email, String password){
        // 1. 이메일로 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorResponseEnum.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException(ErrorResponseEnum.INVALID_PASSWORD);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }
}
