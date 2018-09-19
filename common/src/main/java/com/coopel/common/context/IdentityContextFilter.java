package com.coopel.common.context;

import com.coopel.common.helper.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class IdentityContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IdentityContextFilter.class);

    @Autowired
    private IdentityContextHolder identityContextHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Identity identity = Identity.ANONYMOUS;
            if (RequestHelper.isSecuredResource(request)) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                identity = new IdentityImpl(authentication);
            }

            identityContextHolder.executeWithContext(identity, () -> filterChain.doFilter(request, response));
        } catch (Exception e) {
            log.error("Error creating identity", e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unsupported authority");
        }
    }

}
