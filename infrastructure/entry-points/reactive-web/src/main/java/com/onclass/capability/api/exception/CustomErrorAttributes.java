package com.onclass.capability.api.exception;


import com.onclass.capability.api.dto.response.ApiResponseDto;
import com.onclass.capability.api.utils.HandlersResponseUtil;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.exceptions.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    private static final String STATUS_KEY = "status";
    private static final String BODY_KEY = "body";
    private static final String MSG_INVALID_FIELDS = "Request invalid fields";
    private static final String MSG_INTERNAL_ERROR = "Internal Server Error";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> errorAttributes = new LinkedHashMap<>();

        ApiResponseDto<?> responseBody = switch (error) {
            case BusinessException be -> HandlersResponseUtil.buildBodyFailureResponse(
                    be.getStatusCode().status(),
                    be.getMessage(),
                    null
            );

            case ConstraintViolationException cve -> HandlersResponseUtil.buildBodyFailureResponse(
                    ExceptionStatusCode.FIELDS_BAD_REQUEST.status(),
                    MSG_INVALID_FIELDS,
                    formatConstraintViolations(cve)
            );

            default -> HandlersResponseUtil.buildBodyFailureResponse(
                    ExceptionStatusCode.INTERNAL_SERVER_ERROR.status(),
                    MSG_INTERNAL_ERROR,
                    null
            );
        };

        errorAttributes.put(STATUS_KEY, determineHttpStatus(error));
        errorAttributes.put(BODY_KEY, responseBody);
        return errorAttributes;
    }

    private List<String> formatConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
    }

    private int determineHttpStatus(Throwable error) {
        return switch (error) {
            case BusinessException be -> be.getStatus();
            case ConstraintViolationException ignored -> HttpStatus.BAD_REQUEST.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };
    }
}