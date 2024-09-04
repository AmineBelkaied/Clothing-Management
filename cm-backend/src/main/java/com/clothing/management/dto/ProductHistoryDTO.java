package com.clothing.management.dto;

import com.clothing.management.entities.User;
import jakarta.annotation.Nullable;

import java.util.Date;
import java.util.List;

public class ProductHistoryDTO {

    private  long id;
    private String description;
    private long productId;
    private long quantity;
    private Date date;
    @Nullable
    private User user;
    private String comment;

    public ProductHistoryDTO(long id, String description, long productId, long quantity, Date date, User user, String comment) {
        this.id = id;
        this.description = description;
        this.productId = productId;
        this.quantity = quantity;
        this.date = date;
        this.user = user;
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ProductHistoryDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", date=" + date +
                ", user=" + user +
                ", comment='" + comment + '\'' +
                '}';
    }
}
