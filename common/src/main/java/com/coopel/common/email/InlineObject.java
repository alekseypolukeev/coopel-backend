package com.coopel.common.email;

import com.coopel.common.helper.ValueCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.InputStreamSource;

import java.io.InputStream;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class InlineObject<T> {

    private final String contentId;
    private final String contentType;
    private final ValueCache<T> valueCache;
    private final Function<T, InputStream> streamFactory;

    public InputStreamSource getInputStreamSource() {
        return () -> streamFactory.apply(valueCache.get());
    }
}
