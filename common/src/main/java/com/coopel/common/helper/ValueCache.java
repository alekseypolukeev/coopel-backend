package com.coopel.common.helper;

import javax.inject.Provider;
import java.util.function.Supplier;

public class ValueCache<T> implements Provider<T>, Supplier<T> {

    private final Supplier<T> valueLoader;

    private volatile T value;

    public ValueCache(Supplier<T> valueLoader) {
        this.valueLoader = valueLoader;
    }

    public T getValue() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    result = value = valueLoader.get();
                }
            }
        }
        return result;
    }

    @Override
    public T get() {
        return getValue();
    }
}
