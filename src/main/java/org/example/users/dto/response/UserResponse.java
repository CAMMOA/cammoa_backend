package org.example.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.users.repository.entity.UserEntity;

@AllArgsConstructor
@Getter
public class UserResponse {

    private Long id;
    private String nickname;
    private String username;
    private String email;

    public static UserResponse from(UserEntity user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getUsername(), user.getEmail());
    }
}
