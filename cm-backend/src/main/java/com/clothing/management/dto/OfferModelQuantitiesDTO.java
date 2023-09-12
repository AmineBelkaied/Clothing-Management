package com.clothing.management.dto;

import com.clothing.management.entities.FbPage;

import java.util.List;
import java.util.Set;

public class OfferModelQuantitiesDTO {

    private Long offerId;
    private String name;
    private Double price;

    private Set<FbPage> fbPages;
    private boolean enabled;
    private List<ModelQuantity> modelQuantities;

    public OfferModelQuantitiesDTO() {
    }

    public OfferModelQuantitiesDTO(Long offerId, String name, Double price, boolean enabled, List<ModelQuantity> modelQuantities) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
        this.modelQuantities = modelQuantities;
    }

    public OfferModelQuantitiesDTO(Long offerId, String name, Double price, Set<FbPage> fbPages, boolean enabled) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
        this.fbPages = fbPages;
    }

    public OfferModelQuantitiesDTO(Long offerId, String name, Double price, boolean enabled) {
        this.offerId = offerId;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
    }

    public OfferModelQuantitiesDTO(String name, Double price, boolean enabled) {
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

    public List<ModelQuantity> getModelQuantities() {
        return modelQuantities;
    }

    public void setModelQuantities(List<ModelQuantity> modelQuantities) {
        this.modelQuantities = modelQuantities;
    }

    public Set<FbPage> getFbPages() {
        return fbPages;
    }

    public void setFbPages(Set<FbPage> fbPages) {
        this.fbPages = fbPages;
    }

    @Override
    public String toString() {
        return "OfferModelQuantitiesDTO{" +
                "offerId=" + offerId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", fbPages=" + fbPages +
                ", enabled=" + enabled +
                ", modelQuantities=" + modelQuantities +
                '}';
    }
}
