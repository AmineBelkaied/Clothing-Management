package com.clothing.management.dto;

import java.util.List;

public class StockUpdateDto {
    List<Long> productsId;
    int qte;
    Long modelId;
    String userName;

    public StockUpdateDto(List<Long> productsId, int qte,String userName) {
        this.productsId = productsId;
        this.qte = qte;
        this.userName = userName;
    }

    public List<Long> getProductsId() {
        return productsId;
    }

    public void setProductsId(List<Long> productsId) {
        this.productsId = productsId;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "StockUpdateDto{" +
                "productsId=" + productsId +
                ", qte=" + qte +
                ", modelId=" + modelId +
                ", userName=" + userName +
                '}';
    }
}
