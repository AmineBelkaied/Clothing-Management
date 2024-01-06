package com.clothing.management.auth.mastertenant.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class MasterUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "full_name",nullable = false)
    private String fullName;

    @Column(name = "user_name",nullable = false,unique = true)
    private String userName;

    @Column(name = "password",nullable = false)
    private String password;

    public MasterUser() {
    }

    public MasterUser(String fullName, String userName, String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public MasterUser setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public MasterUser setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public MasterUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MasterUser setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
