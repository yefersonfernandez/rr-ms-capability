package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class TechnologyNotFoundException extends BusinessException {
    public TechnologyNotFoundException(String message) {
        super(ExceptionStatusCode.NOT_FOUND, message, 404);
    }
}
