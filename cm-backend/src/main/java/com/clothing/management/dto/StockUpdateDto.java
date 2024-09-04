package com.clothing.management.dto;

import java.util.List;

public class StockUpdateDto {
    List<Long> productsId;
    Long qte;
    Long modelId;
    String comment;

    public StockUpdateDto(List<Long> productsId, long qte, String comment) {
        this.productsId = productsId;
        this.qte = qte;
        this.comment = comment;
    }

    public List<Long> getProductsId() {
        return productsId;
    }

    public void setProductsId(List<Long> productsId) {
        this.productsId = productsId;
    }

    public Long getQte() {
        return qte;
    }

    public void setQte(long qte) {
        this.qte = qte;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "StockUpdateDto{" +
                "productsId=" + productsId +
                ", qte=" + qte +
                ", modelId=" + modelId +
                ", comment=" + comment +
                '}';
    }
}
