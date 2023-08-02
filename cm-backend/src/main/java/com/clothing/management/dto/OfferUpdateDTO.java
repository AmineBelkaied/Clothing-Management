package com.clothing.management.dto;

import com.clothing.management.entities.Product;

import java.util.List;

public class OfferUpdateDTO {

    private Long offerId;
    private String name;
    private double price;
    private boolean enabled;
    private List<Product> products;

    public OfferUpdateDTO() {
    }

    public OfferUpdateDTO(Long offerId, String name, double price) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
    }

    public OfferUpdateDTO(Long offerId, String name, double price, boolean enabled, List<Product> products) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
        this.products = products;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }


    @Override
    public String toString() {
        return "OfferUpdateDTO{" +
                "offerId=" + offerId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", enabled=" + enabled +
                ", products=" + products +
                '}';
    }
}
