package com.clothing.management.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "model_stock_history", indexes = {
        @Index(name = "idx_model_id", columnList = "model_id")
})
public class ModelStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @Column(name = "model_id")
    private Long modelId;
    @Column(name = "model_name")
    private String modelName;
    private Long quantity;

    public ModelStockHistory() {
    }

    public ModelStockHistory(Long modelId, String modelName, Long quantity) {
        this.date = new Date();
        this.modelId = modelId;
        this.modelName = modelName;
        this.quantity = quantity;
    }

    public ModelStockHistory(Long id,Date date,Long modelId,String modelName, Long quantity) {
        this.id = id;
        this.date = date;
        this.modelId = modelId;
        this.modelName = modelName;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ModelStockHistory{" +
                "id=" + id +
                ", date=" + date +
                ", modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
