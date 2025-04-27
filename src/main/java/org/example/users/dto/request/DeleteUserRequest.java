package org.example.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequest {

    @NotBlank
    private String password;

}
