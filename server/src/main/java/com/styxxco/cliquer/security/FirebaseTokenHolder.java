package com.styxxco.cliquer.security;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.util.ArrayMap;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseTokenHolder {

    @JsonIgnore
    private FirebaseToken token;

    public FirebaseTokenHolder(FirebaseToken token) {
        this.token = token;
    }

    public String getEmail() {
        return token.getEmail();
    }

    public String getName() {
        return token.getName();
    }

    public String getFirstName() {
        return token.getName().split(" ")[0];
    }

    public String getLastName() {
        return token.getName().split(" ")[1];
    }

    public String getUid() {
        return token.getUid();
    }

}
