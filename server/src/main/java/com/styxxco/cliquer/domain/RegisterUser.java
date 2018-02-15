package com.styxxco.cliquer.domain;

public class RegisterUser {
    private final String userName;
    private final String email;

    public RegisterUser(String userName, String email) {
        super();
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
