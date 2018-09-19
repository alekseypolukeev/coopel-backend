package com.coopel.common.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;

public interface ExpressionMethodsFactory {

    String getId();

    Object create(Authentication authentication, MethodInvocation mi);

}
