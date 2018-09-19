package com.coopel.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/common.properties")
public class CommonPropertySourceConfig {
}
