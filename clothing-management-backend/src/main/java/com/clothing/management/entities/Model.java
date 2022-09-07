package com.clothing.management.entities;

import com.clothing.management.enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  name;
    @OneToMany(mappedBy = "model" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<Product> products;
    private String reference;
    private String description;
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "model_colors",
            joinColumns = { @JoinColumn(name = "model_id") },
            inverseJoinColumns = { @JoinColumn(name = "color_id") }
    )
    private Set<Color> colors = new HashSet<>();
    @OneToMany(mappedBy = "model")
    @JsonIgnore
    private Set<OfferModel> modelOffers = new HashSet<>();
    private String size;
    @Transient
    public List<String> sizes = Arrays.asList("S" , "M" , "L" , "1", "2" , "3", "4");

    public Model() {
    }

    public Model(String name, List<Product> products, String reference, String description, Set<Color> colors, Set<OfferModel> modelOffers, String size, List<String> sizes) {
        this.name = name;
        this.products = products;
        this.reference = reference;
        this.description = description;
        this.colors = colors;
        this.modelOffers = modelOffers;
        this.size = size;
        this.sizes = sizes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Set<Color> getColors() {
        return colors;
    }

    public void setColors(Set<Color> colors) {
        this.colors = colors;
    }

    public Set<OfferModel> getModelOffers() { return modelOffers; }

    public void setModelOffers(Set<OfferModel> modelOffers) { this.modelOffers = modelOffers; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reference='" + reference + '\'' +
                ", description='" + description + '\'' +
                ", colors=" + colors +
                ", size=" + size +
                '}';
    }
}
