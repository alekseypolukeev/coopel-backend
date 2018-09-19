package com.coopel.common.exception.handler;

import java.util.List;

public interface CustomExceptionHandler {

    List<Class<? extends Exception>> getSupportedExceptions();

    ErrorResult handle(Exception exception);
}
