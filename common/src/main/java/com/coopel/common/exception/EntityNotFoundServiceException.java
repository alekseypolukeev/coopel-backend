package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class EntityNotFoundServiceException extends RestServiceException {

    public EntityNotFoundServiceException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, message);
    }
}
