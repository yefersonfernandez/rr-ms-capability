package com.onclass.capability.exceptions;

import com.onclass.capability.enums.ExceptionStatusCode;

public class TechnologyMicroserviceException extends BusinessException {
    public TechnologyMicroserviceException(String message) {
        super(ExceptionStatusCode.INTERNAL_SERVER_ERROR, message, 500);
    }
}

