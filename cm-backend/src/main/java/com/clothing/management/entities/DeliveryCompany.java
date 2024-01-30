package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class DeliveryCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String token;
    @JsonIgnore
    @OneToMany(mappedBy = "deliveryCompany")
    List<Packet> packets;

    private boolean enabled;

    public DeliveryCompany() {
    }

    public DeliveryCompany(Long id, String link) {
        this.id = id;
        this.token = token;
        this.enabled = true;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "FbPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", token='" + token + '\''+
                '}';
    }
}
