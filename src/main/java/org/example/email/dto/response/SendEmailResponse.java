package org.example.email.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailResponse {
    private String authCode;
}
