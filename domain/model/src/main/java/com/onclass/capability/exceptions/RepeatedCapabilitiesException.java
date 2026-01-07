package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class RepeatedCapabilitiesException extends BusinessException {
    public RepeatedCapabilitiesException(String message) {
        super(ExceptionStatusCode.CONFLICT, message, 409);
    }
}
