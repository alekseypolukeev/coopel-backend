package com.coopel.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import java.io.IOException;

public class IdentityContextHolder {

    private static final Logger log = LoggerFactory.getLogger(IdentityContextHolder.class);
    private static final IdentityContextHolder INSTANCE = new IdentityContextHolder();

    public static IdentityContextHolder getInstance() {
        return INSTANCE;
    }

    private final ThreadLocal<Identity> threadLocal = new ThreadLocal<>();

    public Identity getIdentity() {
        return threadLocal.get();
    }

    public void executeWithContext(Identity identity, ContextWork work) throws IOException, ServletException {
        MDC.put("user_id", String.valueOf(identity.getUserId()));
        Identity old = threadLocal.get();
        if (old != null) {
            log.error("Identity context re-entrance!!! Was: {} New: {}", old, identity);
        }
        threadLocal.set(identity);
        try {
            work.doWork();
        } finally {
            MDC.remove("user_id");
            threadLocal.remove();
        }
    }

    public interface ContextWork {
        void doWork() throws IOException, ServletException;
    }

    @Configuration
    public static class IdentityContextHolderConfigurer {
        @Bean
        public IdentityContextHolder identityContextHolder() {
            return getInstance();
        }
    }
}
