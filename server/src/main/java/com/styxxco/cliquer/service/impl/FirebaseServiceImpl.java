package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.domain.RegisterUser;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public void registerUser(String firebaseToken) {
        if (StringUtils.isEmpty(firebaseToken)) {
            throw new IllegalArgumentException("FirebaseTokenBlank");
        }
        FirebaseTokenHolder tokenHolder = parseToken(firebaseToken);
        accountService.registerUser(new RegisterUser(tokenHolder.getUid(), tokenHolder.getEmail()));
    }

}
