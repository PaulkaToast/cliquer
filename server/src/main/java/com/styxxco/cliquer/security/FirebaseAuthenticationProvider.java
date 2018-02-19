package com.styxxco.cliquer.security;

import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    @Qualifier(value = AccountServiceImpl.NAME)
    private UserDetailsService accountService;

    public boolean supports(Class<?> authentication) {
        return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
        UserDetails details = accountService.loadUserByUsername(authenticationToken.getName());
        if (details == null) {
            throw new FirebaseUserNotExistsException();
        }

        authenticationToken = new FirebaseAuthenticationToken(details, authentication.getCredentials(),
                details.getAuthorities());

        return authenticationToken;
    }

    public class FirebaseUserNotExistsException extends AuthenticationCredentialsNotFoundException {

        public FirebaseUserNotExistsException() {
            super("User Not Found");
        }

        private static final long serialVersionUID = 789949671713648425L;
    }

}