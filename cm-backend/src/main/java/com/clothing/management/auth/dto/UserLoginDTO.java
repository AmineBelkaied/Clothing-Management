package com.clothing.management.auth.dto;

import java.io.Serializable;

/**
 * @author Md. Amran Hossain
 */
public class UserLoginDTO implements Serializable {

    private String userName;
    private String password;
    private String tenantName;

    public UserLoginDTO() {
    }

    public UserLoginDTO(String userName, String password, String tenantName) {
        this.userName = userName;
        this.password = password;
        this.tenantName = tenantName;
    }

    public String getUserName() {
        return userName;
    }

    public UserLoginDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserLoginDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
