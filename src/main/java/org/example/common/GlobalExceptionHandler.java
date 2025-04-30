package org.example.common;

import org.example.common.repository.entity.CommonResponseEntity;
import org.example.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponseEntity<?>> handleCustomException(CustomException e){
        return ResponseEntity
                .status(e.getError().getHttpStatus())
                .body(CommonResponseEntity.builder()
                        .response(e.getError())
                        .build());
    }

}
