package com.onclass.capability.api.utils;

import com.onclass.capability.api.dto.response.ApiResponseDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class HandlersResponseUtil {

    public static <T> ApiResponseDto<T> buildBodySuccessResponse(String code, T data) {
        return ApiResponseDto
                .<T>builder()
                .message("Operation successful!")
                .data(data)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDto<T> buildBodyFailureResponse(String code, String message, List<String> errors) {
        return ApiResponseDto
                .<T>builder()
                .message(message)
                .errors(errors)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }
}