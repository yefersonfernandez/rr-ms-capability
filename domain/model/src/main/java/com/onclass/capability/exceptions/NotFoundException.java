package com.onclass.capability.exceptions;


import com.onclass.capability.enums.ExceptionStatusCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message, 404);
    }
}

