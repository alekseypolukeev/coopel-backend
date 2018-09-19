package com.coopel.auth.config;

import com.coopel.common.helper.ValueCache;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Supplier;


@Configuration
public class EmailConfig {

    @Bean
    @Qualifier("invitationBodyCache")
    public ValueCache<Template> invitationBodyCache() {
        return new ValueCache<>(() -> compileTemplate(getResourceIS("/email/templates/invitation.html")));
    }

    @Bean
    @Qualifier("passwordRecoveryBodyCache")
    public ValueCache<Template> passwordRecoveryBodyCache() {
        return new ValueCache<>(() -> compileTemplate(getResourceIS("/email/templates/password-recovery.html")));
    }

    @Bean
    @Qualifier("logoInlineCache")
    public ValueCache<byte[]> logoInlineCache() {
        return new ValueCache<>(new Supplier<byte[]>() {
            @Override
            @SneakyThrows
            public byte[] get() {
                InputStream is = getResourceIS("/email/logo.png");
                return IOUtils.toByteArray(is);
            }
        });
    }

    private static Template compileTemplate(InputStream is) {
        return Mustache.compiler().compile(new InputStreamReader(is, Charset.forName("utf-8")));
    }

    private static InputStream getResourceIS(String path) {
        return EmailConfig.class.getResourceAsStream(path);
    }
}
