package com.clothing.management.entities;

import jakarta.persistence.*;
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
    @OneToMany(mappedBy = "offer" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OfferModel> offerModels= new HashSet<>();

    @ManyToMany(cascade = { CascadeType.MERGE },  fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_fb_pages",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "fb_page_id") }
    )
    private Set<FbPage> fbPages = new HashSet<>();

    private Double price;
    private boolean enabled;

    public Offer() {
    }

    public Offer(Long id) {
        this.id = id;
    }

    public Offer(String name, Set<FbPage> fbPages, Double price, boolean enabled) {
        this.name = name;
        this.fbPages = fbPages;
        this.price = price;
        this.enabled = enabled;
    }

    public Offer(String name) {
        this.name = name;
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

    public Set<FbPage> getFbPages() {
        return fbPages;
    }

    public void setFbPages(Set<FbPage> fbPages) {
        this.fbPages = fbPages;
    }

}
