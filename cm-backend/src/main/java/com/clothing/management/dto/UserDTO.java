package com.clothing.management.dto;

import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Role;
import com.clothing.management.entities.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Integer userId;
    private String fullName;
    private String userName;
    private boolean enabled;
    private Set<Role> roles  = new HashSet<>();
    public UserDTO() {
    }
    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.userName = user.getUserName();
        this.enabled = user.isEnabled();
        this.roles = user.getRoles();
    }
    public UserDTO(Integer userId, String fullName, String userName, Boolean enabled, Set<Role> roles) {
        this.userId = userId;
        this.fullName = fullName;
        this.userName = userName;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
