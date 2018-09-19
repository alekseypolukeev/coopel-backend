package com.coopel.common.context;

import com.coopel.common.helper.TokenHelper;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import static java.util.Objects.requireNonNull;

public class IdentityImpl implements Identity {

    private final Authentication authentication;

    public IdentityImpl(Authentication authentication) {
        this.authentication = requireNonNull(authentication);
    }

    @Override
    public int getUserId() {
        return TokenHelper.idFromTokenUsername(authentication.getName());
    }

    @Override
    @SneakyThrows
    public long getTokenIssuedAt() {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) getAuthentication().getDetails();
        return TokenHelper.getIssuedAt(details.getTokenValue());
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }
}
