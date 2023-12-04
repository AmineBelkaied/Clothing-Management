package com.clothing.management.models;

import java.util.Date;

public class ModelsStockCount {
    private Date date;
    private String modelName;
    private long quantity;

    public ModelsStockCount(Date date,String modelName, long quantity ) {
        this.date = date;
        this.modelName = modelName;
        this.quantity = quantity;
    }

    public ModelsStockCount(String modelName, long quantity ) {
        this.date = new Date();
        this.modelName = modelName;
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ModelsStockCount{" +
                "date=" + date +
                ", modelName='" + modelName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
