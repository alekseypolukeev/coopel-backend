package com.coopel.common.context;

import org.springframework.security.core.Authentication;

public interface Identity {

    int NOT_AUTHENTICATED_USER_ID = -1;

    Identity ANONYMOUS = new Identity() {
        @Override
        public int getUserId() {
            return NOT_AUTHENTICATED_USER_ID;
        }

        @Override
        public long getTokenIssuedAt() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Authentication getAuthentication() {
            throw new UnsupportedOperationException();
        }
    };

    int getUserId();

    long getTokenIssuedAt();

    Authentication getAuthentication();

}
