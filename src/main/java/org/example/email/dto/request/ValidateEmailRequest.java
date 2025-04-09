package org.example.email.dto.request;

import lombok.Getter;

@Getter
public class ValidateEmailRequest {
    private String email;
    private String authCode;
}
