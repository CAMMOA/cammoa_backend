package org.example.users.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chat.dto.response.GetChatRoomsResponse;
import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.exception.impl.AuthException;
import org.example.exception.impl.ResourceException;
import org.example.products.dto.response.ProductSimpleResponse;
import org.example.products.service.ParticipationService;
import org.example.security.JwtTokenProvider;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.ChangePasswordRequest;
import org.example.users.dto.request.DeleteUserRequest;
import org.example.users.dto.request.LoginRequest;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.LoginResponse;
import org.example.users.dto.response.ProfileResponse;
import org.example.users.dto.response.UserResponse;
import org.example.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final ParticipationService participationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCreateRequest request) {

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new ResourceException(ErrorResponseEnum.PASSWORDS_DO_NOT_MATCH);
        }

        //회원가입 전 이메일 인증 여부 확인
        if(!userService.isEmailVerified(request.getEmail())){
            throw new AuthException(ErrorResponseEnum.EMAIL_NOT_VERIFIED);
        }

        UserResponse response = userService.signup(request);
        URI location = URI.create("/api/auth/users" + response.getId());

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        LoginResponse loginResponse = userService.login(email, password);

        log.info("request email = {}, password = {}", email, password);
        return ResponseEntity.ok(
            CommonResponseEntity.builder()
                    .data(loginResponse)
                    .response(SuccessResponseEnum.LOGIN_SUCCESS)
                    .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken){
        userService.logout(accessToken);
        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.LOGOUT_SUCCESS)
                        .build()
        );
    }

    @PostMapping("/users/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.PASSWORD_CHANGED)
                        .build()
        );
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @Valid @RequestBody DeleteUserRequest request) {
        userService.deleteUser(userId, request.getPassword());
        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.WITHDRAWAL_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/users")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        ProfileResponse response = userService.getProfile(authorizationHeader);

        return ResponseEntity.ok(
                CommonResponseEntity.<ProfileResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/users/{userId}/group-buyings")
    public ResponseEntity<?> getMyGroupBuyings(@PathVariable Long userId,  @RequestHeader("Authorization") String authorizationHeader) {
        Long tokenUserId = jwtTokenProvider.getUserId(authorizationHeader.replace("Bearer ", ""));
        if (!tokenUserId.equals(userId)) {
            throw new AuthException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }

        List<ProductSimpleResponse> response = userService.getMyGroupBuyings(userId);
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductSimpleResponse>>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    @GetMapping("/users/{userId}/participated-group-buyings")
    public ResponseEntity<?> getParticipatedGroupBuyings(@PathVariable Long userId,  @RequestHeader("Authorization") String authorizationHeader) {
        Long tokenUserId = jwtTokenProvider.getUserId(authorizationHeader.replace("Bearer ", ""));
        if (!tokenUserId.equals(userId)) {
            throw new AuthException(ErrorResponseEnum.UNAUTHORIZED_ACCESS);
        }

        List<ProductSimpleResponse> response = participationService.getParticipatedGroupBuyings(userId);
        return ResponseEntity.ok(
                CommonResponseEntity.<List<ProductSimpleResponse>>builder()
                        .data(response)
                        .response(SuccessResponseEnum.REQUEST_SUCCESS)
                        .build()
        );
    }

    //사용자 채팅방 목록 조회
    @GetMapping("/users/chats")
    public ResponseEntity<?> getChatRooms() {
        List<GetChatRoomsResponse> chatRooms = userService.getChatRooms();

        return ResponseEntity.ok(
                CommonResponseEntity.<List<GetChatRoomsResponse>>builder()
                        .data(chatRooms)
                        .response(SuccessResponseEnum.RESOURCES_GET)
                        .build()
        );
    }
}
