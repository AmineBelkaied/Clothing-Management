package com.clothing.management.dto;

import com.clothing.management.entities.Model;

import java.util.List;

public class OfferModelsDTO {

    private Long offerId;
    private String name;
    private Double price;
    private boolean enabled;
    private List<Model> models;

    public OfferModelsDTO(Long offerId, String name, Double price, boolean enabled, List<Model> models) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
        this.models = models;
    }

    public OfferModelsDTO(Long offerId, String name, Double price, boolean enabled) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
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

    public Double getPrice() {
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

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return "OfferModelsDTO{" +
                "offerId=" + offerId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", enabled=" + enabled +
                ", models=" + models +
                '}';
    }
}
