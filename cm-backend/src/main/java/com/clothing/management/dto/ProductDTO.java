package com.clothing.management.dto;

import com.clothing.management.entities.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDTO {
    private Long id;
    private ModelDTO model;

    int qte;
    private boolean deleted;
    private long modelId;

    private Size size;

    private Color color;

    public ProductDTO() {}

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.model = new ModelDTO(product.getModel());
        this.deleted = product.isDeleted();
        this.qte = product.getQuantity();
        this.color = product.getColor();
        this.size = product.getSize();
        this.modelId = product.getModel().getId();
    }
    public ProductDTO(Product product,boolean needModel) {
        if(needModel)
            this.model = new ModelDTO(product.getModel());
        this.id = product.getId();
        this.deleted = product.isDeleted();
        this.qte = product.getQuantity();
        this.color = product.getColor();
        this.size = product.getSize();
        this.modelId = product.getModel().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModelDTO getModel() {
        return model;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
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
}
