package org.example.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@hufs\\.ac\\.kr$")
    private String email;

}
