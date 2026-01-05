package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class SagaCompensationException extends BusinessException {
    public SagaCompensationException(String message) {
        super(ExceptionStatusCode.INTERNAL_SERVER_ERROR, message, 500);
    }
}
