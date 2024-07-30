package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "model", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIgnoreProperties({
        "hibernateLazyInitializer", "handler" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",scope = Model.class)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  name;

    @JsonBackReference
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private Set<OfferModel> modelOffers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "model" , cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    List<Product> products;

    private String description;
    @Column(name="purchase_price")
    private float purchasePrice;

    @Column(name="earning_coefficient")
    private double earningCoefficient;

    private boolean deleted;

    @ManyToMany(cascade = { CascadeType.MERGE } , fetch = FetchType.EAGER)
    @JoinTable(
            name = "model_colors",
            joinColumns = { @JoinColumn(name = "model_id") },
            inverseJoinColumns = { @JoinColumn(name = "color_id") }
    )
    private List<Color> colors = new ArrayList<>();
    @ManyToMany(cascade = { CascadeType.MERGE } , fetch = FetchType.EAGER)
    @JoinTable(
            name = "model_sizes",
            joinColumns = { @JoinColumn(name = "model_id") },
            inverseJoinColumns = { @JoinColumn(name = "size_id") }
    )
    private List<Size> sizes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductHistory> productHistories;

    public Model() {
        this.deleted = false;
        this.earningCoefficient = 1.5;
        this.purchasePrice = 15;
    }

    public Model(Long id) {
        this.id = id;
    }
    public Model(Model model) {
        this.name = model.getName();
        this.description = model.getDescription();
        this.purchasePrice = model.getPurchasePrice();
        this.earningCoefficient = model.getEarningCoefficient();
        this.deleted = model.isDeleted();
    }
    public Model(String name,
                 List<Product> products,
                 String description,
                 List<Color> colors,
                 List<Size> sizes,
                 Set<OfferModel> modelOffers,
                 List<ProductHistory> productHistories,
                 float purchasePrice,
                 double earningCoefficient,boolean deleted) {
        this.name = name;
        this.products = products;
        this.description = description;
        this.colors = colors;
        this.sizes = sizes;
        this.modelOffers = modelOffers;
        this.productHistories = productHistories;
        this.purchasePrice = purchasePrice;
        this.earningCoefficient = earningCoefficient;
        this.deleted = deleted;
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

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
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

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getEarningCoefficient() {
        return earningCoefficient;
    }

    public void setEarningCoefficient(double gainCoefficient) {
        this.earningCoefficient = gainCoefficient;
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
        if (!(o instanceof Model model)) return false;
        return Float.compare(model.purchasePrice, purchasePrice) == 0 &&
                Double.compare(model.earningCoefficient, earningCoefficient) == 0 &&
                Objects.equals(id, model.id) &&
                Objects.equals(name, model.name) &&
                Objects.equals(description, model.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, purchasePrice, earningCoefficient);
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", gainCoefficient=" + earningCoefficient +
                ", deleted=" + deleted +
                '}';
    }
}
