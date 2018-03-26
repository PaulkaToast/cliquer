package com.styxxco.cliquer.security;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

public class FirebaseParser {

    public FirebaseTokenHolder parseToken(String idToken) {
        if (StringUtils.isEmpty(idToken)) {
            throw new IllegalArgumentException("FirebaseTokenBlank");
        }
        try {
            ApiFuture<FirebaseToken> authTask = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken);
            while (!authTask.isDone());
            return new FirebaseTokenHolder(authTask.get());
        } catch (Exception e) {
            throw new FirebaseTokenInvalidException(e.getMessage());
        }
    }

    public class FirebaseTokenInvalidException extends BadCredentialsException {

        public FirebaseTokenInvalidException(String msg) {
            super(msg);
        }

        private static final long serialVersionUID = 789949671713648425L;

    }
}