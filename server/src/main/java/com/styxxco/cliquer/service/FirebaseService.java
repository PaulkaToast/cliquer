package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Role;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface FirebaseService {

    FirebaseTokenHolder parseToken(String idToken);

    void registerUser(String firebaseToken);
    Account getUser(String uid);
    Collection<? extends GrantedAuthority> getAnonRoles();
    Collection<? extends GrantedAuthority> getModRoles();
    Collection<? extends GrantedAuthority> getUserRoles();
    Collection<? extends GrantedAuthority> getUserRoles(String username);


}
