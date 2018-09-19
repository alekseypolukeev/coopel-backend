package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class IllegalStateServiceException extends RestServiceException {

    public IllegalStateServiceException(String message) {
        super(HttpServletResponse.SC_NOT_ACCEPTABLE, message);
    }
}