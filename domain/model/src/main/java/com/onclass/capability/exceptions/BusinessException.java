package com.onclass.capability.exceptions;


import com.onclass.capability.enums.ExceptionStatusCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ExceptionStatusCode statusCode;
    private final int status;

    public BusinessException(ExceptionStatusCode statusCode, String message, int status) {
        super(message);
        this.statusCode = statusCode;
        this.status = status;
    }
}