package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name="product_history")
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    @Column(name = "last_modification_date")
    private Date lastModificationDate;


    @JoinColumn(name = "model_id")
    @JsonIgnore
    @ManyToOne
    Model model;
    private String userName;
    public ProductHistory() {
    }

    public ProductHistory(Product product, int quantity, Date lastModificationDate, Model model,String userName) {
        this.product = product;
        this.quantity = quantity;
        this.lastModificationDate = lastModificationDate;
        this.model = model;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "ProductHistory{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", lastModificationDate=" + lastModificationDate +
                ", userName=" + userName +
                '}';
    }
}
