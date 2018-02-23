package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.security.FirebaseParser;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Autowired
    @Qualifier(value = AccountServiceImpl.NAME)
    private AccountService accountService;

    @Override
    public FirebaseTokenHolder parseToken(String firebaseToken) {
        return new FirebaseParser().parseToken(firebaseToken);
    }

    @Transactional
    @Override
    public Account registerUser(String firebaseToken, String firstName, String lastName) {
        if (StringUtils.isEmpty(firebaseToken)) {
            throw new IllegalArgumentException("FirebaseTokenBlank");
        }
        FirebaseTokenHolder tokenHolder = parseToken(firebaseToken);
        return accountService.registerUser(tokenHolder, firstName, lastName);
    }

    @Override
    public Collection<? extends GrantedAuthority> getUserRoles(String username) {
        Account account = accountService.getUserProfile(username);
        if (account == null) {
            return null;
        }
        return account.getAuthorities();
    }

    @Override
    public Account getUser(String uid) {
        return accountService.getUserProfile(uid);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAnonRoles() {
        Collection<? extends GrantedAuthority> roles = accountService.getAnonRoles();
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getUserRoles() {
        Collection<? extends GrantedAuthority> roles = accountService.getUserRoles();
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getModRoles() {
        Collection<? extends GrantedAuthority> roles = accountService.getModRoles();
        return roles;
    }

}
