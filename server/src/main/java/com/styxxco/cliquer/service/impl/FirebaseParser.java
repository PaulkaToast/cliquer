package com.styxxco.cliquer.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

public class FirebaseParser {

    public FirebaseTokenHolder parseToken(String idToken) {
        if (StringUtils.isEmpty(idToken)) {
            throw new IllegalArgumentException("FirebaseTokenBlank");
        }
        try {
            Task<FirebaseToken> authTask = FirebaseAuth.getInstance().verifyIdToken(idToken);

            Tasks.await(authTask);

            return new FirebaseTokenHolder(authTask.getResult());
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