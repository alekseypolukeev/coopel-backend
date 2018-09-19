package com.coopel.jpa.service.validation;

import com.coopel.common.exception.ValidationErrorTypes;
import com.coopel.common.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.Lob;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StringFieldValidationService<E> {
    private static final Logger log = LoggerFactory.getLogger(StringFieldValidationService.class);

    private List<StringPropertyMetadata> stringProperties;

    public void validate(E entity) {
        for (StringPropertyMetadata metadata : stringProperties(entity)) {
            String propertyName = metadata.getPropertyName();
            try {
                String propertyValue = (String) metadata.getMethod().invoke(entity);
                if (propertyValue != null) {
                    if (propertyValue.length() > metadata.getLength()) {
                        String message = String.format("%s length can't be greater than %s", propertyName, metadata.getLength());
                        throw new ValidationException(message, ValidationErrorTypes.LENGTH_CONSTRAINT, propertyName);
                    }
                    if (propertyValue.isEmpty()) {
                        String message = String.format("%s can't be blank", propertyName);
                        throw new ValidationException(message, ValidationErrorTypes.BLANK_CHARS_CONSTRAINT, propertyName);
                    }
                    String trimmed = StringUtils.trim(propertyValue);
                    if (trimmed.length() != propertyValue.length()) {
                        String message = String.format("%s must be trimmed", propertyName);
                        throw new ValidationException(message, ValidationErrorTypes.BLANK_CHARS_CONSTRAINT, propertyName);
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                String message = String.format("Unable to get value of parameter %s", propertyName);
                log.error(message, e);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private class StringPropertyMetadata {
        private String propertyName;
        private Integer length;
        private Boolean nullable;
        private Method method;
    }

    private synchronized List<StringPropertyMetadata> stringProperties(E entity) {
        if (stringProperties == null) {
            stringProperties = new ArrayList<>();
            ReflectionUtils.doWithFields(
                    entity.getClass(),
                    // Field callback
                    field -> {
                        StringPropertyMetadata metadata = this.mapToMetadata(entity.getClass(), field);
                        if (metadata != null) {
                            stringProperties.add(metadata);
                        }
                    },
                    // Filter callback
                    field -> !field.getType().isInterface()
                            && field.getType().isAssignableFrom(String.class)
                            && field.getAnnotation(Column.class) != null
                            && field.getAnnotation(Lob.class) == null
            );
        }
        return stringProperties;
    }

    private StringPropertyMetadata mapToMetadata(Class<?> clazz, Field field) {
        String name = field.getName();
        String getterName = "get" + StringUtils.capitalize(name);
        Method getter = ReflectionUtils.findMethod(clazz, getterName);
        if (getter == null) {
            log.error("Unable to find {} in class {} or it's superclasses", getterName, clazz.getName());
            return null;
        }
        Column columnAnnotation = field.getAnnotation(Column.class);
        return new StringPropertyMetadata(name, columnAnnotation.length(), columnAnnotation.nullable(), getter);
    }
}
