package com.coopel.jpa.exception;

public class EntityValidationException extends RuntimeException {
    private String constraintName;
    private String columns;

    public EntityValidationException(String message) {
        super(message);
    }

    public EntityValidationException(String message, String constraintName, String columns) {
        super(message);
        this.constraintName = constraintName;
        this.columns = columns;
    }

    public String getErrorCode() {
        return (constraintName != null && columns != null) ? constraintName+": "+columns : null;
    }
}
