package com.clothing.management.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "offer" , cascade = CascadeType.ALL)
    private Set<OfferModel> offerModels= new HashSet<>();
    private Double price;
    private boolean enabled;

    public Offer() {
    }

    public Offer(Long id) {
        this.id = id;
    }

    public Offer(String name, Double price, boolean enabled) {
        this.name = name;
        this.price = price;
        this.enabled = enabled;
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

    public Set<OfferModel> getOfferModels() {
        return offerModels;
    }

    public void setOfferModels(Set<OfferModel> offerModels) {
        this.offerModels = offerModels;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;
        Offer offer = (Offer) o;
        return isEnabled() == offer.isEnabled() && getId().equals(offer.getId()) && getName().equals(offer.getName()) && getOfferModels().equals(offer.getOfferModels()) && getPrice().equals(offer.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getOfferModels(), getPrice(), isEnabled());
    }
}
