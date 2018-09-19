package com.coopel.common.exception.handler;

import com.coopel.common.exception.ValidationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Component
public class ValidationExceptionHandler implements CustomExceptionHandler {

    @Override
    public ErrorResult handle(Exception exception) {
        ValidationException eve = (ValidationException) exception;
        return new ErrorResult(
                eve.getMessage(),
                HttpServletResponse.SC_BAD_REQUEST,
                eve.getErrorCode()
        );
    }

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(ValidationException.class);
    }
}
