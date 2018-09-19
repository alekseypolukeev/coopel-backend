package com.coopel.common.exception;

import lombok.Getter;

@Getter
public abstract class RestServiceException extends RuntimeException {

    private final int status;
    private final String message;

    RestServiceException(int status, String message) {
        super(status + ": " + message);
        this.status = status;
        this.message = message;
    }
}
