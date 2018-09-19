package com.coopel.common.exception.handler;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Component
public class DefaultExceptionHandler implements CustomExceptionHandler {

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(Exception.class);
    }

    @Override
    public ErrorResult handle(Exception exception) {
        return new ErrorResult(
                exception.getMessage(),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
    }
}
