package com.coopel.common.exception.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorResult {

    private final String message;
    private final int httpStatus;
    private final String errorCode;

    @Setter
    private String stackTrace;

    public ErrorResult(String message, int httpStatus) {
        this(message, httpStatus, null, null);
    }

    public ErrorResult(String message, int httpStatus, String errorCode) {
        this(message, httpStatus, errorCode, null);
    }

    private ErrorResult(String message, int httpStatus, String errorCode, String stackTrace) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.stackTrace = stackTrace;
    }
}
