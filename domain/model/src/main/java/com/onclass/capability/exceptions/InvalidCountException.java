package com.onclass.capability.exceptions;


import com.onclass.capability.enums.ExceptionStatusCode;

public class InvalidCountException extends BusinessException {
    public InvalidCountException(String message) {
        super(ExceptionStatusCode.BAD_REQUEST, message, 400);
    }
}

