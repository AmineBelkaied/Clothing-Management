package com.clothing.management.dto;

import com.clothing.management.entities.Model;

import java.util.List;

public class ModelQuantity {

    private Integer quantity;
    private Model model;

    //TODO list products

    public ModelQuantity() {
    }

    public ModelQuantity(Integer quantity, Model model) {
        this.quantity = quantity;
        this.model = model;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ModelQuantity{" +
                "quantity=" + quantity +
                ", model=" + model +
                '}';
    }
}