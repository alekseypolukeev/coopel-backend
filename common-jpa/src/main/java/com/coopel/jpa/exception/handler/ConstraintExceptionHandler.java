package com.coopel.jpa.exception.handler;

import com.coopel.common.exception.handler.CustomExceptionHandler;
import com.coopel.common.exception.handler.ErrorResult;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static com.coopel.common.exception.ValidationErrorTypes.*;

@Component
public class ConstraintExceptionHandler implements CustomExceptionHandler {

    @Override
    public ErrorResult handle(Exception exception) {
        DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) exception;
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) dataIntegrityViolationException.getCause();
        String causeErrorMessage = constraintViolationException.getCause().getMessage();
        String errorCode = null;
        if (isNullConstraint(constraintViolationException)) {
            errorCode = NOT_NULL_CONSTRAINT + ": " + constraintViolationException.getConstraintName();
        }
        if (isUKConstraint(constraintViolationException)) {
            errorCode = UK_CONSTRAINT + ": " + getColumnsConstraint(causeErrorMessage);
        }
        if (isFKConstraint(constraintViolationException)) {
            errorCode = FK_CONSTRAINT + ": " + getColumnsConstraint(causeErrorMessage);
        }
        String errorMessage = causeErrorMessage.substring(causeErrorMessage.indexOf("Detail: ") + 8);
        String message = String.format("Unable to save or update entity due to: %s", errorMessage);

        return new ErrorResult(
                message,
                HttpServletResponse.SC_BAD_REQUEST,
                errorCode
        );
    }

    private String getColumnsConstraint(String causeErrorMessage) {
        int leftBracketIdx = causeErrorMessage.indexOf('(');
        int rightBracketIdx = causeErrorMessage.indexOf(')');
        return (leftBracketIdx == -1 || rightBracketIdx == -1) ?
                "N/A" : causeErrorMessage.substring(leftBracketIdx + 1, rightBracketIdx);
    }

    private boolean isNullConstraint(ConstraintViolationException e) {
        Throwable sqlException = e.getCause();
        return sqlException != null && sqlException.getMessage().contains("not-null constraint");
    }

    private boolean isUKConstraint(ConstraintViolationException e) {
        Throwable sqlException = e.getCause();
        return sqlException != null && sqlException.getMessage().contains("unique constraint");
    }

    private boolean isFKConstraint(ConstraintViolationException e) {
        Throwable sqlException = e.getCause();
        return sqlException != null && sqlException.getMessage().contains("foreign key constraint");
    }

    @Override
    public List<Class<? extends Exception>> getSupportedExceptions() {
        return Collections.singletonList(DataIntegrityViolationException.class);
    }
}
