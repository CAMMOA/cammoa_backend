package org.example.common.ResponseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponseEnum implements Response {

    RESPONSE_NOT_VALID(HttpStatus.BAD_REQUEST, "Response Is Not Valid");

    private final HttpStatus httpStatus;
    private final String message;
}
