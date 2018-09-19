package com.coopel.common.exception;

public interface ValidationErrorTypes {
    String UK_CONSTRAINT = "uk-constraint";
    String FK_CONSTRAINT = "fk-constraint";
    String NOT_NULL_CONSTRAINT = "not-null-constraint";
    String LENGTH_CONSTRAINT = "length-constraint";
    String BLANK_CHARS_CONSTRAINT = "blank-chars-constraint";
    String COMMON = "validation-error";
}
