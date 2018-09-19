package com.coopel.common.helper;

import javax.annotation.Nullable;
import java.util.Objects;

public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * @return true in case of list of nulls
     */
    public static boolean isEmpty(Object... list) {
        for (Object o : list) {
            if (Objects.nonNull(o)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return true in case of list of non-nulls
     */
    public static boolean isNotEmpty(Object... list) {
        for (Object o : list) {
            if (Objects.isNull(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean nullToFalse(@Nullable Boolean v) {
        if (v == null) {
            return false;
        }
        return v;
    }

    public static long nullToZero(@Nullable Long v) {
        if (v == null) {
            return 0L;
        }
        return v;
    }

    public static int nullToZero(@Nullable Integer v) {
        if (v == null) {
            return 0;
        }
        return v;
    }

}
