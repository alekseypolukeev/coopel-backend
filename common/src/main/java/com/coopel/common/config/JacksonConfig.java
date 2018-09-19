package com.coopel.common.config;

import com.coopel.common.EnvironmentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TimeZoneConfig.class)
public class JacksonConfig {

    @Bean
    public static Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(EnvironmentType environmentType) {
        return builder -> {
            //builder.modules(new JavaTimeModule());
            builder.failOnUnknownProperties(false);
            if (!environmentType.isSafe()) {
                builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            }
        };
    }
}
