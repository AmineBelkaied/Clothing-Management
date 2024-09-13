package com.clothing.management.dto;

import com.clothing.management.entities.Product;

public class ProductResponse {
    private Long id;
    private Long modelId;
    private Long sizeId;
    private Long colorId;
    private Long qte;

    public ProductResponse(Long id, Long modelId, Long sizeId, Long colorId, Long qte) {
        this.id = id;
        this.modelId = modelId;
        this.sizeId = sizeId;
        this.colorId = colorId;
        this.qte = qte;
    }

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.modelId = product.getModel().getId();
        this.sizeId = product.getSize() != null ? product.getSize().getId() : null;
        this.colorId = product.getColor() != null ? product.getColor().getId() : null;
        this.qte = product.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public Long getQte() {
        return qte;
    }

    public void setQte(Long qte) {
        this.qte = qte;
    }
}
