package com.clothing.management.dto;

import com.clothing.management.entities.Size;

import java.util.List;
import java.util.Set;

public class StockDTO {
    List<List<Object>> productsByColor;
    List<Size> sizes;

    public StockDTO() {
    }

    public StockDTO(List<List<Object>> productsByColor, List<Size> sizes) {
        this.productsByColor = productsByColor;
        this.sizes = sizes;
    }

    public List<List<Object>> getProductsByColor() {
        return productsByColor;
    }

    public void setProductsByColor(List<List<Object>> productsByColor) {
        this.productsByColor = productsByColor;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "StockDTO{" +
                "productsByColor=" + productsByColor +
                ", sizes=" + sizes +
                '}';
    }
}
