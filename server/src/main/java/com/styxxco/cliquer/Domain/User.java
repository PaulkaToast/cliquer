package com.styxxco.cliquer.Domain;

public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private int reputation;

    private User() {}

    public User(String firstName, String lastName, int reputation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.reputation = reputation;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, firstName='%s', lastName='%s', reputation=%s]", id, firstName, lastName, reputation);
    }
}
