package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "fb_page")
public class FbPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String link;

    @JsonIgnore
    @OneToMany(mappedBy = "fbPage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Packet> packets;

    @ManyToMany(mappedBy = "fbPages", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Offer> offers = new HashSet<>();

    @Column(nullable = false)
    private boolean enabled;

    // Default constructor
    public FbPage() {
    }

    // Constructor for id and link
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

    public List<Packet> getPackets() {
        return packets;
    }

    public void setPackets(List<Packet> packets) {
        this.packets = packets;
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

    @Override
    public boolean equals(Object o) {
        System.out.println("oFBPage"+o);
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
        return "FbPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
