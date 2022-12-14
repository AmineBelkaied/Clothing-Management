package com.clothing.management.entities;

import com.clothing.management.enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String size;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;
    private int quantity;
    private Date date;

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductsPacket> commands;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "model_id")
    private Model model;

    public Product() {
    }

    public Product(String reference, String size, Color color, int quantity, Date date, Set<ProductsPacket> commands, Model model) {
        this.reference = reference;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.date = date;
        this.commands = commands;
        this.model = model;
    }

    public Product(Model model, Color color, String size) {
        this.model = model;
        this.color = color;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
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

    public Set<ProductsPacket> getCommands() {
        return commands;
    }

    public void setCommands(Set<ProductsPacket> commands) {
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
                ", reference='" + reference + '\'' +
                ", size=" + size +
                ", color='" + color + '\'' +
                ", quantity=" + quantity +
                ", date=" + date +
                ", commands=" + commands +
                ", model=" + model +
                '}';
    }
}
