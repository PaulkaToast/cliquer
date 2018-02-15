package com.styxxco.cliquer.service;

import com.styxxco.cliquer.security.FirebaseTokenHolder;

public interface FirebaseService {

    FirebaseTokenHolder parseToken(String idToken);

    void registerUser(String firebaseToken);


}
