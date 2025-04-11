package org.example.common;

import org.example.exception.CustomException;
import org.example.exception.impl.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponseEntity<?>> handleCustomException(CustomException e){
        return ResponseEntity
                .status(e.getResponse().getHttpStatus())
                .body(CommonResponseEntity.builder()
                        .response(e.getResponse())
                        .build());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<?> handleInvalidRequest(InvalidRequestException ex) {
        LinkedHashMap<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error", "INVALID_REQUEST");
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);

    }

}
