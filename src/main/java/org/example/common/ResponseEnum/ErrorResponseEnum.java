package org.example.common.ResponseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponseEnum implements Response {

    RESPONSE_NOT_VALID(HttpStatus.BAD_REQUEST, "Response Is Not Valid"),

    //이메일
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed To Send Email"),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "Email Verification Failed"),

    //인증코드
    AUTH_CODE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication Code Cannot Be Found"),
    AUTH_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "Authentication Code Does Not Match");

    private final HttpStatus httpStatus;
    private final String message;
}
