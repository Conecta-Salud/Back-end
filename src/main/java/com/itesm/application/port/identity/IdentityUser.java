package com.itesm.application.port.identity;

public class IdentityUser {

    private final String uid;
    private final String email;

    public IdentityUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}
