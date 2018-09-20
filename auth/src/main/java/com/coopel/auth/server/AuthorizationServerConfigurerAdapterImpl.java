package com.coopel.auth.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component
@EnableAuthorizationServer
public class AuthorizationServerConfigurerAdapterImpl extends AuthorizationServerConfigurerAdapter {

    public static final String AUTH_SERVICE_RESOURCE_ID = "auth";

    public static final String CONFIRM_EMAIL_CLIENT_ID = "email-confirm";
    public static final String CONFIRM_EMAIL_SCOPE = "confirmEmail";

    public static final String PASSWORD_RECOVERY_CLIENT_ID = "pwd-recovery";
    public static final String PASSWORD_RECOVERY_SCOPE = "recoverPwd";

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter tokenEnhancer;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Inject
    public AuthorizationServerConfigurerAdapterImpl(
            TokenStore tokenStore,
            JwtAccessTokenConverter tokenEnhancer,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager
    ) {
        this.tokenStore = tokenStore;
        this.tokenEnhancer = tokenEnhancer;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("frontend")
                .secret(passwordEncoder.encode("change_me"))
                .authorizedGrantTypes("refresh_token", "password")
                .resourceIds("auth")
                .scopes("create", "get", "update", "delete", "do")
                .accessTokenValiditySeconds(30 * 60)
                .refreshTokenValiditySeconds(30 * 24 * 60 * 60)

                .and()
                .withClient(CONFIRM_EMAIL_CLIENT_ID)
                .secret("{none}" + UUID.randomUUID())
                .resourceIds(AUTH_SERVICE_RESOURCE_ID)
                .scopes(CONFIRM_EMAIL_SCOPE)
                .accessTokenValiditySeconds(24 * 60 * 60)

                .and()
                .withClient(PASSWORD_RECOVERY_CLIENT_ID)
                .secret("{none}" + UUID.randomUUID())
                .resourceIds(AUTH_SERVICE_RESOURCE_ID)
                .scopes(PASSWORD_RECOVERY_SCOPE)
                .accessTokenValiditySeconds(24 * 60 * 60);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .tokenEnhancer(tokenEnhancer)
                .reuseRefreshTokens(false) // TODO ugly hack - see token store, need to verify refresh token issued at
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .accessDeniedHandler(oauthAccessDeniedHandler())
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }

}
