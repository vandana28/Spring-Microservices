package com.vandanasridhar.photoapp.api.users.ui.model;

public class LoginRequestModel {// login request is by email id and password. that will be provided in the json payload. now the same fields must be present in the login request model.

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
