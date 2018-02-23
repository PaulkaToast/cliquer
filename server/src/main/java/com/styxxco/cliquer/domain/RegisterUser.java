package com.styxxco.cliquer.domain;

public class RegisterUser {
    private final String userName;
    private final String email;
    private final String firstName;
    private final String lastName;

    public RegisterUser(String userName, String email, String firstName, String lastName) {
        super();
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
