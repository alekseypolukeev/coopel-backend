package com.coopel.common.exception;

import javax.servlet.http.HttpServletResponse;

public class ModificationConflictServiceException extends RestServiceException {

    public ModificationConflictServiceException() {
        super(HttpServletResponse.SC_CONFLICT, "Outdated object version");
    }
}