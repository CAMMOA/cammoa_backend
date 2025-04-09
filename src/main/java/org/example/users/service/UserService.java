package org.example.users.service;

import jakarta.mail.MessagingException;
import org.example.users.dto.request.UserCreateRequest;
import org.example.users.dto.response.UserResponse;

public interface UserService {
    UserResponse signup(UserCreateRequest request);
    boolean sendAuthcode(String email) throws MessagingException;
}
