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
    AUTH_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "Authentication Code Does Not Match"),

    //사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    //게시글
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 올바른 타입을 입력해주세요."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
