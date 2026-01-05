package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class CapabilityAlreadyExistsException extends BusinessException {
    public CapabilityAlreadyExistsException(String message) {
        super(ExceptionStatusCode.CONFLICT, message, 409);
    }
}

