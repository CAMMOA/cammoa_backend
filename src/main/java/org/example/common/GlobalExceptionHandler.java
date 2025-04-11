package org.example.common;

import org.example.common.ResponseEnum.ErrorResponseEnum;
import org.example.exception.CustomException;
import org.example.exception.impl.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;

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
