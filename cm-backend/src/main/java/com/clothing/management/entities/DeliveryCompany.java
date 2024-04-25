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
    @JsonIgnore
    @OneToMany(mappedBy = "deliveryCompany")
    List<Packet> packets;

    public DeliveryCompany() {
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

    public List<Packet> getPackets() {
        return packets;
    }

    public void setPackets(List<Packet> packets) {
        this.packets = packets;
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

    @Override
    public String toString() {
        return "DeliveryCompany{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", apiName='" + apiName + '\'' +
                ", token='" + token + '\''+
                '}';
    }
}
