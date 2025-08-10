package com.smartsupplychain.riskintelligence.security;

import java.util.Map;

public class FirebaseUserPrincipal {
    private final String uid;
    private final String email;
    private final Map<String, Object> claims;

    public FirebaseUserPrincipal(String uid, String email, Map<String, Object> claims) {
        this.uid = uid;
        this.email = email;
        this.claims = claims;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public String getName() {
        return (String) claims.getOrDefault("name", email);
    }
}