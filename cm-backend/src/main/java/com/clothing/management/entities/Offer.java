package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "offer", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OfferModel> offerModels = new HashSet<>();

    @JsonIgnore
    @JsonBackReference
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductsPacket> productsPacket = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_fb_pages",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "fb_page_id") }
    )
    private Set<FbPage> fbPages = new HashSet<>();

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean deleted;

    public Offer() {
        this.enabled = false;
        this.deleted = false;
        this.price = 0.0;
    }

    public Offer(Long id) {
        this.id = id;
    }

    public Offer(String name, Set<FbPage> fbPages, Double price, boolean enabled,boolean deleted) {
        this.name = name;
        this.fbPages = fbPages != null ? fbPages : new HashSet<>();
        this.price = price;
        this.enabled = enabled;
        this.deleted = deleted;
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

    public List<ProductsPacket> getProductsPacket() {
        return productsPacket;
    }

    public void setProductsPacket(List<ProductsPacket> productsPacket) {
        this.productsPacket = productsPacket;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return enabled == offer.enabled &&
                Objects.equals(id, offer.id) &&
                Objects.equals(name, offer.name) &&
                Objects.equals(price, offer.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, enabled);
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fbPages=" + fbPages +
                ", price=" + price +
                ", enabled=" + enabled +
                ", deleted=" + deleted +
                '}';
    }
}
