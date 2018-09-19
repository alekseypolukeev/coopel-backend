package com.coopel.common.config;

import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    static {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(utcTimeZone);
    }
}
