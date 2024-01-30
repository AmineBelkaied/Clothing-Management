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
    @Column(name = "product_id")
    private Long productId;
    private String reference;
    private int quantity;
    @Column(name = "last_modification_date")
    private Date lastModificationDate;
    @ManyToOne
    @JoinColumn(name = "model_id")
    @JsonIgnore
    Model model;
    private String userName;
    public ProductHistory() {
    }

    public ProductHistory(Long productId, String reference, int quantity, Date lastModificationDate, Model model,String userName) {
        this.productId = productId;
        this.reference = reference;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
                ", productId=" + productId +
                ", reference='" + reference + '\'' +
                ", quantity=" + quantity +
                ", lastModificationDate=" + lastModificationDate +
                ", userName=" + userName +
                '}';
    }
}
