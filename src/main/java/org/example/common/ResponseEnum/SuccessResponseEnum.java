package org.example.common.ResponseEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessResponseEnum implements Response {
    RESOURCES_CREATED(HttpStatus.CREATED, "Resources Created"),
    LOGIN_SUCCESS(HttpStatus.OK, "Login Successful"),
    PASSWORD_CHANGED(HttpStatus.OK, "Password Changed"),
    WITHDRAWAL_SUCCESS(HttpStatus.OK, "User Withdrawal Success"),

    EMAIL_SEND_SUCCESS(HttpStatus.OK, "Email Successfully Sent"),
    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "Email Verification Successed"),
    REQUEST_SUCCESS(HttpStatus.OK, "Request Processed Successfully"),

    POST_DELETE_SUCCESS(HttpStatus.OK, "Post Deleted Successfully");

    private final HttpStatus httpStatus;
    private final String message;
}
