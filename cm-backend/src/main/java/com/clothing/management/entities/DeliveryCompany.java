package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.util.List;

@Table(name = "delivery_company")
@Entity
public class DeliveryCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String token;
    @Column(name = "api_name")
    private String apiName;
    @Column(name = "barre_code_url")
    private String barreCodeUrl;

    private boolean deleted;

    @Column(name = "additional_name")
    private String additionalName;

    public DeliveryCompany() {
        deleted=false;
    }

    public DeliveryCompany(String name, String token, String apiName) {
        this.name = name;
        this.token = token;
        this.apiName = apiName;
    }

    public DeliveryCompany(String name) {
        this.name = name;
    }

    public DeliveryCompany(String name,String token) {
        this.name = name;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getBarreCodeUrl() {
        return barreCodeUrl;
    }

    public void setBarreCodeUrl(String barreCodeUrl) {
        this.barreCodeUrl = barreCodeUrl;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "DeliveryCompany{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", apiName='" + apiName + '\'' +
                ", barreCodeUrl='" + barreCodeUrl + '\'' +
                ", additionalName='" + additionalName + '\'' +
                '}';
    }
}
