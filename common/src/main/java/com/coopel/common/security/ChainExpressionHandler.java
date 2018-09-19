package com.coopel.common.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class ChainExpressionHandler extends OAuth2MethodSecurityExpressionHandler {

    private final List<ExpressionMethodsFactory> factories;

    @Inject
    public ChainExpressionHandler(List<ExpressionMethodsFactory> factories) {
        this.factories = factories;
        setDefaultRolePrefix("");
    }

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, MethodInvocation mi) {
        StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
        for (ExpressionMethodsFactory factory : factories) {
            ec.setVariable(factory.getId(), factory.create(authentication, mi));
        }
        return ec;
    }

}
