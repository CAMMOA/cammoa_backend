package org.example.users.service;

import jakarta.mail.MessagingException;
import org.example.email.dto.request.ValidateEmailRequest;
import org.example.email.dto.response.SendEmailResponse;
import org.example.security.dto.JwtToken;
import org.example.users.dto.request.ChangePasswordRequest;
import org.example.users.dto.request.DeleteUserRequest;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.ProfileResponse;
import org.example.users.dto.response.UserResponse;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.AuthenticationException;

public interface UserService {
    UserResponse signup(UserCreateRequest request);
    SendEmailResponse sendAuthcode(String email) throws MessagingException;
    void validationAuthCode(@RequestBody ValidateEmailRequest request);
    JwtToken login(String email, String password);
    void changePassword(@RequestBody ChangePasswordRequest request);
    void deleteUser(Long userId, String password);
    ProfileResponse getProfile(String authorizationHeader);
}
