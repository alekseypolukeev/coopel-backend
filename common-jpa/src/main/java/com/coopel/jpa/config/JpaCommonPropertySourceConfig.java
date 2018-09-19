package com.coopel.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/common-jpa.properties")
public class JpaCommonPropertySourceConfig {
}
