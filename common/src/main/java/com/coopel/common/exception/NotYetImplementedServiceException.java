package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class NotYetImplementedServiceException extends RestServiceException {

    public NotYetImplementedServiceException() {
        super(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
    }

    public NotYetImplementedServiceException(String message) {
        super(HttpServletResponse.SC_NOT_IMPLEMENTED, message);
    }
}
