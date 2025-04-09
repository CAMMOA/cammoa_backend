package org.example.email.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailResponse {
    private String messageId;
    private String authCode;
}
