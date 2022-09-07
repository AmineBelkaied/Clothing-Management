package com.clothing.management.dto;

import java.util.List;

public class OfferModelDTO {

    private Long offerId;
    private String name;
    private Float price;
    private boolean enabled;
    private List<ModelQuantity> modelQuantities;

    public OfferModelDTO() {
    }

    public OfferModelDTO(Long offerId, String name, Float price, boolean enabled, List<ModelQuantity> modelQuantities) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
        this.modelQuantities = modelQuantities;
    }

    public OfferModelDTO(Long offerId, String name, Float price, boolean enabled) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
    }

    public OfferModelDTO(String name, Float price, boolean enabled) {
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<ModelQuantity> getModelQuantities() {
        return modelQuantities;
    }

    public void setModelQuantities(List<ModelQuantity> modelQuantities) {
        this.modelQuantities = modelQuantities;
    }

    @Override
    public String toString() {
        return "OfferModelDTO{" +
                "offerId=" + offerId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", enabled=" + enabled +
                ", modelQuantities=" + modelQuantities +
                '}';
    }
}
