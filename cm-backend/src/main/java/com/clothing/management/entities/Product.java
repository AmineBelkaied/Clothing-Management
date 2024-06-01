package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",scope = Product.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    private Size size;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id")
    private Color color;
    private int quantity;
    private Date date;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductsPacket> commands;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductHistory> productHistory;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
    }

    public Product(Size size, Color color, int quantity, Date date, Model model) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.date = date;
        this.commands = commands;
        this.model = model;
    }

    public Product(Model model, Color color, Size size) {
        this.model = model;
        this.color = color;
        this.size = size;
    }

    public Product(Long id, int quantity) {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ProductsPacket> getCommands() {
        return commands;
    }

    public void setCommands(List<ProductsPacket> commands) {
        this.commands = commands;
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", date=" + date +
                '}';
    }

}
