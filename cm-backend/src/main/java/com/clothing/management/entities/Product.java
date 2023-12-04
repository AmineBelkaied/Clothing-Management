package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",scope = Product.class)
public class Product {

    private String reference;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private String reference;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id")
    private Size size;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;
    private int quantity;
    private Date date;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductsPacket> commands;
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
    }

    public Product(String reference, Size size, Color color, int quantity, Date date, Model model) {
        this.reference = reference;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.date = date;
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

    public String getReference() {return reference;}

    public void setReference(String reference) {this.reference = reference;}

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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", reference=" + reference +
                ", quantity=" + quantity +
                ", date=" + date +
                '}';
    }

}
