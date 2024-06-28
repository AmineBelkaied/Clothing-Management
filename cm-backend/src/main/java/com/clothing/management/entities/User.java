package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 8925284124120199772L;

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

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> roles  = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PacketStatus> packetStatusList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductHistory> productHistoryList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Note> notes;

    public User() {
    }

    public User(String fullName, String userName, String password, Boolean enabled) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
    }

    public Integer getUserId() {
        return userId;
    }

    public User setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PacketStatus> getPacketStatusList() {
        return packetStatusList;
    }

    public void setPacketStatusList(List<PacketStatus> packetStatusList) {
        this.packetStatusList = packetStatusList;
    }

    public List<ProductHistory> getProductHistoryList() {
        return productHistoryList;
    }

    public void setProductHistoryList(List<ProductHistory> productHistoryList) {
        this.productHistoryList = productHistoryList;
    }

    public List<Note> getNotes() {
        return notes;
    };
    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", enabled='" + enabled + '\'' +
                ", roles=" + roles +
                '}';
    }
}
