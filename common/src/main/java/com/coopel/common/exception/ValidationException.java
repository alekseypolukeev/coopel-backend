package com.coopel.common.exception;

public class ValidationException extends RuntimeException {
    private String errorType;
    private String columns;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, String errorType, String columns) {
        super(message);
        this.errorType = errorType;
        this.columns = columns;
    }

    public String getErrorCode() {
        return (errorType != null && columns != null) ? errorType + ": " + columns : null;
    }
}
