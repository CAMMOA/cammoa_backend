package org.example.users.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.CommonResponseEntity;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.email.dto.SendEmailResponse;
import org.example.email.service.EmailService;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;
import org.example.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
        SendEmailResponse response = emailService.sendSimpleMessage(email);

        return ResponseEntity.ok(
                CommonResponseEntity.<SendEmailResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.EMAIL_SEND_SUCCESS)
                        .build()
        );
    }
}
