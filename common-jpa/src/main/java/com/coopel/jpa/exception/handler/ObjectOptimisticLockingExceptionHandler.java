package com.coopel.jpa.exception.handler;

import com.coopel.common.exception.handler.CustomExceptionHandler;
import com.coopel.common.exception.handler.ErrorResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Component
public class ObjectOptimisticLockingExceptionHandler implements CustomExceptionHandler {

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(ObjectOptimisticLockingFailureException.class);
    }

    @Override
    public ErrorResult handle(Exception exception) {
        return new ErrorResult(
                "Outdated object version",
                HttpServletResponse.SC_CONFLICT
        );
    }
}
