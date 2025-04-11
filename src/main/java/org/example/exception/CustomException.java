package org.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;
import org.example.common.ResponseEnum.ErrorResponseEnum;

@Getter
public class CustomException extends RuntimeException {
    private final Response error;

    public CustomException(Response error) {
        super(error.getMessage());
        this.error = error;
    }

    public Response getError() {
        return error;
    }
}
