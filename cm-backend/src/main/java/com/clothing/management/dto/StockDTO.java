package com.clothing.management.dto;

import com.clothing.management.entities.Model;
import com.clothing.management.entities.Size;

import java.util.List;
import java.util.Set;

public class StockDTO {

    Model model;
    List<List<ProductDTO>> productsByColor;
    List<Size> sizes;

    public StockDTO() {
    }

    public StockDTO(List<List<ProductDTO>> productsByColor, List<Size> sizes) {

        this.productsByColor = productsByColor;
        this.sizes = sizes;
    }

    public List<List<ProductDTO>> getProductsByColor() {
        return productsByColor;
    }

    public void setProductsByColor(List<List<ProductDTO>> productsByColor) {
        this.productsByColor = productsByColor;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "StockDTO{" +
                "productsByColor=" + productsByColor +
                ", sizes=" + sizes +
                '}';
    }
}
