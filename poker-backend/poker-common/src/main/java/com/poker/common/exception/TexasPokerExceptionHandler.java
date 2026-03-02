package com.poker.common.exception;

import com.poker.common.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for all controllers across all services.
 * This is automatically applied to any Spring Boot application that includes poker-common.
 */
@RestControllerAdvice
@Slf4j
public class TexasPokerExceptionHandler {

    /**
     * Handle business exceptions
     */
    @ExceptionHandler(TexasPokerException.class)
    public ApiResult<?> handleBusinessException(TexasPokerException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return ApiResult.error(400, e.getMessage());
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return ApiResult.error(500, "Internal server error: " + e.getMessage());
    }
}
