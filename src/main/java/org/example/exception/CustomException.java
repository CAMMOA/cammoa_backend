package org.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final Response response;
}
