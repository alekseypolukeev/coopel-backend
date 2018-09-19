package com.coopel.auth.config;

import com.coopel.auth.server.SpecialUserDetails;
import com.coopel.common.helper.TokenHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.inject.Inject;
import java.io.IOException;

@Configuration
public class AuthTokenStoreConfig {

    @Value("${coopel.auth.keystore.path}")
    private String keystorePath;

    @Value("${coopel.auth.keystore.secret}")
    private String certificateSecret;

    @Value("${coopel.auth.keystore.key-alias}")
    private String keyAlias;

    @Inject
    private ResourceLoader resourceLoader;

    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        final Resource keyStore = resourceLoader.getResource(keystorePath);

        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyStore, certificateSecret.toCharArray());

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                OAuth2AccessToken adjustedToken = TokenHelper.addIssuedAtInfo(accessToken);
                return super.enhance(adjustedToken, authentication);
            }
        };
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(keyAlias));
        return converter;
    }

    @Bean
    TokenStore tokenStore() throws IOException {
        // TODO ugly hack - need to verify refresh token issued at. Won't work without {@code reuseRefreshTokens(false)}
        return new JwtTokenStore(jwtAccessTokenConverter()) {

            private final ThreadLocal<Long> tokenIssuedAt = new ThreadLocal<>();

            @Override
            public OAuth2RefreshToken readRefreshToken(String tokenValue) {
                tokenIssuedAt.set(TokenHelper.getIssuedAt(tokenValue));
                return super.readRefreshToken(tokenValue);
            }

            @Override
            public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
                OAuth2Request oAuth2Request = authentication.getOAuth2Request();
                if (oAuth2Request.getRefreshTokenRequest() != null) {
                    SpecialUserDetails userDetails = (SpecialUserDetails) authentication.getPrincipal();
                    Long notBefore = userDetails.getTokensNotBefore();
                    if (notBefore != null) {
                        Long issuedAt = tokenIssuedAt.get();
                        if (issuedAt == null) {
                            throw new IllegalStateException("issuedAt wasn't set");
                        }
                        if (issuedAt < notBefore) {
                            throw new InvalidTokenException("token has been revoked");
                        }
                    }
                }
                tokenIssuedAt.remove();

                super.storeRefreshToken(refreshToken, authentication);
            }
        };
    }
}
