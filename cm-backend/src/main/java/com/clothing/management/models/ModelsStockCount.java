package com.clothing.management.models;

import java.util.Date;

public class modelStockCount {
    private Date date;
    private Long modelId;
    private Long quantity;

    public modelStockCount( Long modelId, Long quantity ) {
        this.date = new Date();
        this.modelId = modelId;
        this.quantity = quantity;
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
