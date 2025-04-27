package org.example.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@hufs\\.ac\\.kr$")
    private String email;

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String newPassword;
}
