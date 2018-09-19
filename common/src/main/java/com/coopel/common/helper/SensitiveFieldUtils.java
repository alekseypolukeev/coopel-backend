package com.coopel.common.helper;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class SensitiveFieldUtils {

    private SensitiveFieldUtils() {
    }

    @Nullable
    public static String toUpperCase(@Nullable String v) {
        String trimmed = trim(v);
        return trimmed == null ? null : trimmed.toUpperCase();
    }

    public static String toUpperCaseMandatory(String v) {
        return trimMandatory(v).toUpperCase();
    }

    @Nullable
    public static String toLowerCase(@Nullable String v) {
        String trimmed = trim(v);
        return trimmed == null ? null : trimmed.toLowerCase();
    }

    public static String toLowerCaseMandatory(String v) {
        return trimMandatory(v).toLowerCase();
    }

    @Nullable
    public static String trim(@Nullable String v) {
        String trimmed = StringUtils.trim(v);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }

    public static String trimMandatory(String v) {
        String trimmed = StringUtils.trim(v);
        if (trimmed == null || trimmed.isEmpty()) {
            throw new IllegalArgumentException("value is null or blank");
        }
        return trimmed;
    }

}
