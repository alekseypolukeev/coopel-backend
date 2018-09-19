package com.coopel.auth.service;

import com.coopel.auth.dto.UserDto;
import com.coopel.common.email.ByteArrayInlineObject;
import com.coopel.common.email.EmailService;
import com.coopel.common.helper.TokenHelper;
import com.coopel.common.helper.ValueCache;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coopel.auth.server.AuthorizationServerConfigurerAdapterImpl.*;

@Component
public class AuthEmailService {

    private static final Logger log = LoggerFactory.getLogger(AuthEmailService.class);

    private final EmailService emailService;
    private final AuthorizationServerTokenServices tokenServices;

    private final ValueCache<byte[]> logoInlineCache;
    private final ValueCache<Template> invitationBodyCache;
    private final ValueCache<Template> passwordRecoveryBodyCache;

    private final String siteUrl;
    private final String passwordRecoveryPath;
    private final String confirmEmailFormPath;
    private final String supportEmail;

    @Inject
    public AuthEmailService(
            EmailService emailService,
            AuthorizationServerTokenServices tokenServices,
            @Qualifier("logoInlineCache") ValueCache<byte[]> logoInlineCache,
            @Qualifier("invitationBodyCache") ValueCache<Template> invitationBodyCache,
            @Qualifier("passwordRecoveryBodyCache") ValueCache<Template> passwordRecoveryBodyCache,
            @Value("${coopel.auth.email.site-url}") String siteUrl,
            @Value("${coopel.auth.email.password-recovery-path}") String passwordRecoveryPath,
            @Value("${coopel.auth.email.confirm-email-path}") String confirmEmailFormPath,
            @Value("${common.email.support}") String supportEmail
    ) {
        this.emailService = emailService;
        this.logoInlineCache = logoInlineCache;
        this.invitationBodyCache = invitationBodyCache;
        this.passwordRecoveryBodyCache = passwordRecoveryBodyCache;
        this.tokenServices = tokenServices;
        this.siteUrl = siteUrl;
        this.confirmEmailFormPath = confirmEmailFormPath;
        this.supportEmail = supportEmail;
        this.passwordRecoveryPath = passwordRecoveryPath;
    }

    public void sendPasswordRecoveryEmail(UserDto user) throws Exception {
        Map<String, String> context = new HashMap<>();
        context.put("siteUrl", siteUrl);
        context.put("firstName", user.getFirstName());
        context.put("lastName", user.getLastName());
        context.put("actionLink", buildPasswordRecoveryUrl(user));
        context.put("supportLink", String.format("mailto:%s", supportEmail));

        String body = passwordRecoveryBodyCache.get().execute(context);

        emailService.sendHtml(
                "Восстановление пароля",
                user.getEmail(),
                body,
                new ByteArrayInlineObject("logo", "image/png", logoInlineCache)
        );
    }

    private String buildPasswordRecoveryUrl(UserDto user) {
        String accessToken = createAccessToken(
                user,
                PASSWORD_RECOVERY_CLIENT_ID,
                PASSWORD_RECOVERY_SCOPE,
                AUTH_SERVICE_RESOURCE_ID
        );
        return siteUrl + passwordRecoveryPath + "?token=" + accessToken;
    }

    public void sendInvitationEmail(UserDto user, boolean firstTime) throws Exception {
        Map<String, Object> context = new HashMap<>();
        context.put("siteUrl", siteUrl);
        context.put("firstName", user.getFirstName());
        context.put("lastName", user.getLastName());
        context.put("actionLink", buildConfirmEmailUrl(user));
        context.put("supportLink", String.format("mailto:%s", supportEmail));
        context.put("firstTime", firstTime);

        String subject = firstTime ? "Добро пожаловать" : "Подтверждение Email";
        String body = invitationBodyCache.get().execute(context);

        emailService.sendHtml(
                subject,
                user.getEmail(),
                body,
                new ByteArrayInlineObject("logo", "image/png", logoInlineCache)
        );
    }

    private String buildConfirmEmailUrl(UserDto user) {
        String accessToken = createAccessToken(
                user,
                CONFIRM_EMAIL_CLIENT_ID,
                CONFIRM_EMAIL_SCOPE,
                AUTH_SERVICE_RESOURCE_ID
        );
        return siteUrl + confirmEmailFormPath + "?token=" + accessToken;
    }

    private String createAccessToken(UserDto user, String clientId, String scope, String resourceId) {
        List<SimpleGrantedAuthority> authorities = user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String tokenUsername = TokenHelper.tokenUsernameFromId(user.getId());

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                new OAuth2Request(
                        Collections.emptyMap(),
                        clientId,
                        authorities,
                        false,
                        Collections.singleton(scope),
                        Collections.singleton(resourceId),
                        null,
                        Collections.emptySet(),
                        null
                ),
                new UsernamePasswordAuthenticationToken(
                        new User(
                                tokenUsername,
                                "",
                                true,
                                true,
                                true,
                                true,
                                authorities
                        ),
                        null,
                        authorities
                ));
        authenticationRequest.setAuthenticated(true);

        return tokenServices.createAccessToken(authenticationRequest).getValue();
    }

}
