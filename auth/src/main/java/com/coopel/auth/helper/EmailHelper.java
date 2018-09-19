package com.coopel.auth.helper;

import com.coopel.common.exception.ValidationException;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.coopel.common.exception.ValidationErrorTypes.COMMON;
import static com.coopel.common.helper.SensitiveFieldUtils.toLowerCase;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class EmailHelper {

    // RFC 5322 complaint email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$", CASE_INSENSITIVE);

    private EmailHelper() {
    }

    public static void check(@Nullable String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format", COMMON, "email");
        }
    }

    public static String adjust(@Nullable String email){
        return toLowerCase(email);
    }
}
