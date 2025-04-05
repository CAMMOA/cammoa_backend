package org.example.common;

import org.example.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
