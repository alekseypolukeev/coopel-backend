package com.coopel.common.server;

import com.coopel.common.EnvironmentType;
import com.coopel.common.exception.handler.CustomExceptionHandler;
import com.coopel.common.exception.handler.ErrorResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String HTTP_STATUS_FIELD_NAME = "http_status";
    private static final Logger log = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    private final Map<Class<? extends Exception>, CustomExceptionHandler> handlersMap = new HashMap<>();

    private final EnvironmentType environmentType;

    @Inject
    public ServiceExceptionHandler(List<CustomExceptionHandler> customExceptionHandlers, EnvironmentType environmentType) {
        this.environmentType = environmentType;
        loadExceptionHandlers(customExceptionHandlers);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleControllerException(HttpServletRequest request, Exception e) {
        CustomExceptionHandler handler = getExceptionHandler(e.getClass());

        ErrorResult errorResult = handler.handle(e);

        if (environmentType.isSafe()) {
            errorResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        }

        MDC.put(HTTP_STATUS_FIELD_NAME, String.valueOf(errorResult.getHttpStatus()));
        try {
            String errorMessage = String.format("%s occurred. Use %s handler to handle it.", e.getClass().getName(), handler.getClass().getName());
            log.error(errorMessage, e);
        } finally {
            MDC.remove(HTTP_STATUS_FIELD_NAME);
        }

        return ResponseEntity.status(errorResult.getHttpStatus()).body(errorResult);
    }

    private void loadExceptionHandlers(List<CustomExceptionHandler> customExceptionHandlers) {
        for (CustomExceptionHandler handler : customExceptionHandlers) {
            for (Class<? extends Exception> exceptionClass : handler.getSupportedExceptions()) {
                if (handlersMap.put(exceptionClass, handler) != null) {
                    throw new IllegalStateException(String.format("Handler for %s already registered", exceptionClass.getName()));
                }
            }
        }
    }

    private CustomExceptionHandler getExceptionHandler(Class<? extends Exception> exceptionClass) {
        CustomExceptionHandler handler = handlersMap.get(exceptionClass);
        if (handler == null) {
            handler = handlersMap.get(findClosestParentException(exceptionClass));
        }
        return handler;
    }

    private Class<? extends Exception> findClosestParentException(Class<? extends Exception> exceptionClass) {
        List<Class<? extends Exception>> parentClasses = handlersMap.keySet().stream()
                .filter(clazz -> clazz.isAssignableFrom(exceptionClass))
                .collect(Collectors.toList());

        Map<? extends Class<? extends Exception>, Integer> inheritanceMap = parentClasses.stream().collect(
                Collectors.toMap(
                        clazz -> clazz,
                        clazz -> inheritanceDistance(clazz, exceptionClass)));

        return inheritanceMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .min((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
    }

    private static int inheritanceDistance(Class<? extends Exception> parent, Class<?> son) {
        int distance = 0;
        Class<?> _parent = son.getSuperclass();
        while (!parent.equals(_parent)) {
            distance++;
            _parent = _parent.getSuperclass();
        }

        return distance;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        MDC.put(HTTP_STATUS_FIELD_NAME, status.toString());
        try {
            log.warn("Spring handled error:", ex);
            return super.handleExceptionInternal(ex, body, headers, status, request);
        } finally {
            MDC.remove(HTTP_STATUS_FIELD_NAME);
        }
    }

}