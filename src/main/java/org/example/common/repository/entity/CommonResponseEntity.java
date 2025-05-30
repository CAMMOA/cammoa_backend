package org.example.common.repository.entity;

import lombok.Builder;
import lombok.Getter;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponseEntity<T> {

    private final HttpStatus status;
    private final String message;
    private final T data;

    @Builder
    public CommonResponseEntity(Response response, T data) {
        this.status = response.getHttpStatus();
        this.message = response.getMessage();
        this.data = data;
    }


}



