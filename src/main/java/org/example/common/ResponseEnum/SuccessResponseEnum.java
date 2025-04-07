package org.example.common.ResponseEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessResponseEnum implements Response {
    RESOURCES_CREATED(HttpStatus.CREATED, "Resources Created");

    private final HttpStatus httpStatus;
    private final String message;
}
