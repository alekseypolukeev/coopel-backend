package com.coopel.common.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApplicationContextHeaderCustomFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Application-Context";

    private final String headerValue;

    public ApplicationContextHeaderCustomFilter(
            ApplicationContext context,
            @Value("${common.service.version}") String version,
            @Value("${common.service.id}") String nodeId
    ) {
        headerValue = version + ":" + context.getId() + ":" + nodeId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HEADER_NAME, headerValue);
        filterChain.doFilter(request, response);
    }
}
