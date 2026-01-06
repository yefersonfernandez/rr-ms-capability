package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class CapabilityTechnologiesCountException extends BusinessException {
    public CapabilityTechnologiesCountException(String message) {
        super(ExceptionStatusCode.BAD_REQUEST, message, 400);
    }
}
