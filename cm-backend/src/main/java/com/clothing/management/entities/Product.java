package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",scope = Product.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    private Size size;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id")
    private Color color;
    private Long quantity;
    private Date date;

    @JsonIgnore
    @JsonBackReference
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductsPacket> productsPacket;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductHistory> productHistory;
    private boolean deleted;

    public Product() {
        deleted = false;
    }

    public Product(Long id) {
        this.id = id;
    }

    public Product(Size size, Color color, long quantity, Date date, Model model) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.date = date;
        this.productsPacket = productsPacket;
        this.model = model;
    }

    public Product(Model model, Color color, Size size) {
        this.model = model;
        this.color = color;
        this.size = size;
    }

    public Product(Long id, long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ProductsPacket> getProductsPacket() {
        return productsPacket;
    }

    public void setProductsPacket(List<ProductsPacket> commands) {
        this.productsPacket = commands;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public List<ProductHistory> getProductHistory() {
        return productHistory;
    }

    public void setProductHistory(List<ProductHistory> productHistory) {
        this.productHistory = productHistory;
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
        if (!(o instanceof Product product)) return false;
        return quantity == product.quantity && deleted == product.deleted && id.equals(product.id) && size.equals(product.size) && color.equals(product.color) && date.equals(product.date) && model.equals(product.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, date, deleted);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", date=" + date +
                ", deleted=" + deleted +
                '}';
    }

}
