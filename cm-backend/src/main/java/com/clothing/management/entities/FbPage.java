package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "fb_page", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
public class FbPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String link;

    @JsonIgnore
    @OneToMany(mappedBy = "fbPage", fetch = FetchType.LAZY)
    private List<Packet> packets;

    @JsonIgnore
    @ManyToMany(mappedBy = "fbPages", fetch = FetchType.LAZY)
    private Set<Offer> offers = new HashSet<>();

    @Column(nullable = false)
    private boolean enabled;

    private boolean deleted;

    // Default constructor
    public FbPage() {
        deleted = false;
    }

    // Constructor for id and link
    public FbPage(FbPage fbPage) {
        this.id = fbPage.getId();
        this.name = fbPage.getName();
        this.enabled = fbPage.isEnabled();
    }

    public FbPage(Long id, String link) {
        this.id = id;
        this.link = link;
        this.enabled = true;
    }

    public FbPage(Long id, String name, String link, boolean enabled) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.enabled = enabled;
    }

    // Constructor for name
    public FbPage(String name) {
        this.name = name;
    }

    // Getters and Setters
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
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
        FbPage fbPage = (FbPage) o;
        return enabled == fbPage.enabled &&
                Objects.equals(id, fbPage.id) &&
                Objects.equals(name, fbPage.name) &&
                Objects.equals(link, fbPage.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, enabled);
    }

    @Override
    public String toString() {
        return name;
    }
}
