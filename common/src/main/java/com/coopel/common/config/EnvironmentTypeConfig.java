package com.coopel.common.config;

import com.coopel.common.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EnvironmentTypeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentTypeConfig.class);

    private static final String PRODUCTION_ENV_NAME = "prod";
    private static final String DEVELOPMENT_ENV_NAME = "dev";
    private static final String TEST_ENV_NAME = "test";

    @Bean
    @Profile(PRODUCTION_ENV_NAME)
    private static EnvironmentType prodEnvironmentType() {
        LOGGER.info("Application started in Production mode");
        return EnvironmentType.Production;
    }

    @Bean
    @Profile(DEVELOPMENT_ENV_NAME)
    private static EnvironmentType devEnvironmentType() {
        LOGGER.info("Application started in Development mode");
        return EnvironmentType.Development;
    }

    @Bean
    @Profile(TEST_ENV_NAME)
    private static EnvironmentType testEnvironmentType() {
        LOGGER.info("Application started in Test mode");
        return EnvironmentType.Test;
    }

    @Bean
    @ConditionalOnMissingBean
    private static EnvironmentType defaultEnvironmentType() {
        LOGGER.info("Application started in Default mode");
        return EnvironmentType.Default;
    }
}
