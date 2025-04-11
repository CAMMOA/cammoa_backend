package org.example.common.ResponseEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessResponseEnum implements Response {
    RESOURCES_CREATED(HttpStatus.CREATED, "Resources Created"),

    EMAIL_SEND_SUCCESS(HttpStatus.OK, "Email Successfully Sent"),
    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "Email Verification Successed"),

    OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
