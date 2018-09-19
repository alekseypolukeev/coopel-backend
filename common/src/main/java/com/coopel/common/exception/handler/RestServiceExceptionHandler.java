package com.coopel.common.exception.handler;

import com.coopel.common.exception.RestServiceException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RestServiceExceptionHandler implements CustomExceptionHandler {

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(RestServiceException.class);
    }

    @Override
    public ErrorResult handle(Exception exception) {
        RestServiceException restServiceException = (RestServiceException) exception;
        return new ErrorResult(
                restServiceException.getMessage(),
                restServiceException.getStatus()
        );
    }
}
