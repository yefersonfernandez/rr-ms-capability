package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class RepeatedTechnologiesException extends BusinessException {
    public RepeatedTechnologiesException(String message) {
        super(ExceptionStatusCode.CONFLICT, message, 409);
    }
}
