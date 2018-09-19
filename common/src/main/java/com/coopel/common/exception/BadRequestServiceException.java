package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class BadRequestServiceException extends RestServiceException {

    public BadRequestServiceException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}
