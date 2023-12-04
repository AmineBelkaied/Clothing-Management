package com.clothing.management.auth.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Md. Amran Hossain
 */
public class AuthResponse implements Serializable {

    private String userName;

    private Set<String> roles;
    private String token;

    public AuthResponse(String userName, String token) {
        this.userName = userName;
        this.token = token;
    }

    public AuthResponse(String userName, Set<String> roles, String token) {
        this.userName = userName;
        this.roles = roles;
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public AuthResponse setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getToken() {
        return token;
    }

    public AuthResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public AuthResponse setRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }
}
