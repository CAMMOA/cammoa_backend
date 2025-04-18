package org.example.users.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.email.service.EmailService;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.LoginRequest;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;
import org.example.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCreateRequest request){

        UserResponse response = userService.signup(request);
        URI location = URI.create("api/auth/users" + response.getId());

        return ResponseEntity.created(location)
                .body(CommonResponseEntity.<UserResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.RESOURCES_CREATED)
                        .build());

    }

    @GetMapping("/signup/email/{email}")
    public ResponseEntity<?> sendEmail(@PathVariable String email) throws MessagingException {
        //이메일 전송 및 결과 받기
        SendEmailResponse response = userService.sendAuthcode(email);

        return ResponseEntity.ok(
                CommonResponseEntity.<SendEmailResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.EMAIL_SEND_SUCCESS)
                        .build()
        );
    }

    @PostMapping("/signup/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody ValidateEmailRequest request) {
        userService.validationAuthCode(request);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.EMAIL_VERIFICATION_SUCCESS)
                        .build()
        );
    }

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        JwtToken jwtToken = userService.login(email, password);

        log.info("request email = {}, password = {}", email, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        return jwtToken;
    }
}
