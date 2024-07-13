package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "model")
@JsonIgnoreProperties({
        "hibernateLazyInitializer", "handler" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",scope = Model.class)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  name;
    @OneToMany(mappedBy = "model" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Product> products;
    private String reference;
    private String description;
    @Column(name="purchase_price")
    private float purchasePrice;

    @Column(name="earning_coefficient")
    private double earningCoefficient;

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

    public Model(String name, List<Product> products, String reference, String description, List<Color> colors, List<Size> sizes, Set<OfferModel> modelOffers, List<ProductHistory> productHistories, ModelImage image, float purchasePrice,double earningCoefficient) {
        this.name = name;
        this.products = products;
        this.reference = reference;
        this.description = description;
        this.colors = colors;
        this.sizes = sizes;
        this.modelOffers = modelOffers;
        this.productHistories = productHistories;
        this.image = image;
        this.purchasePrice = purchasePrice;
        this.earningCoefficient = earningCoefficient;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model model)) return false;
        return Float.compare(model.purchasePrice, purchasePrice) == 0 && Double.compare(model.earningCoefficient, earningCoefficient) == 0 && Objects.equals(id, model.id) && Objects.equals(name, model.name) && Objects.equals(products, model.products) && Objects.equals(reference, model.reference) && Objects.equals(description, model.description) && Objects.equals(colors, model.colors) && Objects.equals(sizes, model.sizes) && Objects.equals(modelOffers, model.modelOffers) && Objects.equals(productHistories, model.productHistories) && Objects.equals(image, model.image) && Arrays.equals(bytes, model.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, products, reference, description, purchasePrice, earningCoefficient, colors, sizes, modelOffers, productHistories, image);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
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
                ", purchasePrice=" + purchasePrice +
                ", gainCoefficient=" + earningCoefficient +
                '}';
    }
}
