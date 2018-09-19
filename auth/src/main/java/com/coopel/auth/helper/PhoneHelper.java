package com.coopel.auth.helper;

import com.coopel.common.exception.ValidationException;

import javax.annotation.Nullable;

import static com.coopel.common.exception.ValidationErrorTypes.COMMON;
import static com.coopel.common.helper.SensitiveFieldUtils.toLowerCase;

public class PhoneHelper {

    private PhoneHelper() {
    }

    public static void check(@Nullable String phone) {
        if (phone != null && phone.length() != 11) {
            throw new ValidationException("Invalid phone format", COMMON, "phone");
        }
    }

    public static String adjust(@Nullable String phone) {
        return toLowerCase(phone);
    }
}
