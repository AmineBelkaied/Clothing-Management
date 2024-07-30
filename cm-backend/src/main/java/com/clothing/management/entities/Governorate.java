package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Entity
@Table(name = "governorate", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Governorate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int delivery_id;

    @Column(name = "jax_code")
    private int jaxCode;

    @JsonIgnore
    @OneToMany(mappedBy = "governorate" , cascade = CascadeType.ALL)
    private List<City> cities;

    public Governorate() {
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

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public int getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(int delivery_id) {
        this.delivery_id = delivery_id;
    }

    public int getJaxCode() {
        return jaxCode;
    }

    public void setJaxCode(int jaxCode) {
        this.jaxCode = jaxCode;
    }

    @Override
    public String toString() {
        return "Governorate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", delivery_id=" + delivery_id +
                ", jaxCode='" + jaxCode + '\'' +
                '}';
    }
}
