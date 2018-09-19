package com.coopel.auth.helper;

import com.coopel.common.exception.ValidationException;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.coopel.common.exception.ValidationErrorTypes.COMMON;

public class PasswordHelper {

    private PasswordHelper() {
    }

    public static void check(@Nullable String password) {
        if (password != null && (password.length() < 8 || password.length() > 63)) {
            throw new ValidationException("Password must be 8-63 characters", COMMON, "password");
        }
    }

    public static String adjust(@Nullable String password) {
        if (password == null) {
            return UUID.randomUUID().toString();
        }
        return password;
    }

}
