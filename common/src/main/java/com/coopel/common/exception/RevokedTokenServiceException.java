package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class RevokedTokenServiceException extends RestServiceException {

    public RevokedTokenServiceException(String message) {
        super(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

}
