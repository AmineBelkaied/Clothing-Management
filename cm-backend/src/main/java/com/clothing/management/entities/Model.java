package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Model.class)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  name;
    @OneToMany(mappedBy = "model" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "model_sizes",
            joinColumns = { @JoinColumn(name = "model_id") },
            inverseJoinColumns = { @JoinColumn(name = "size_id") }
    )
    private Set<Size> sizes = new HashSet<>();
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<OfferModel> modelOffers = new HashSet<>();

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProductHistory> productHistories;

    @OneToOne(mappedBy = "model", cascade = CascadeType.ALL)
    private ModelImage image;
    @Transient
    private byte[] bytes;
    public Model() {
    }

    public Model(Long id) {
        this.id = id;
    }

    public Model(String name, List<Product> products, String reference, String description, Set<Color> colors, Set<Size> sizes, Set<OfferModel> modelOffers, List<ProductHistory> productHistories, ModelImage image) {
        this.name = name;
        this.products = products;
        this.reference = reference;
        this.description = description;
        this.colors = colors;
        this.sizes = sizes;
        this.modelOffers = modelOffers;
        this.productHistories = productHistories;
        this.image = image;
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

    public Set<Size> getSizes() {
        return sizes;
    }

    public void setSizes(Set<Size> sizes) {
        this.sizes = sizes;
    }

    public ModelImage getImage() {
        return image;
    }

    public void setImage(ModelImage image) {
        this.image = image;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reference='" + reference + '\'' +
                ", description='" + description + '\'' +
                ", colors=" + colors +
                ", sizes=" + sizes +
                '}';
    }
}
