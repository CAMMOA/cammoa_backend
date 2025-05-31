package org.example.users.service;

import jakarta.mail.MessagingException;
import org.example.chat.dto.response.GetChatRoomsResponse;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.products.dto.response.ProductSimpleResponse;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.ChangePasswordRequest;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.LoginResponse;
import org.example.users.dto.response.ProfileResponse;
import org.example.users.dto.response.UserResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserService {
    UserResponse signup(UserCreateRequest request);
    SendEmailResponse sendAuthcode(String email) throws MessagingException;
    void validationAuthCode(@RequestBody ValidateEmailRequest request);
    boolean isEmailVerified(String email);
    LoginResponse login(String email, String password);
    void logout(String accessToken);
    void changePassword(@RequestBody ChangePasswordRequest request);
    void deleteUser(Long userId, String password);
    ProfileResponse getProfile(String authorizationHeader);
    List<ProductSimpleResponse> getMyGroupBuyings(Long userId);
    List<GetChatRoomsResponse> getChatRooms();
}
