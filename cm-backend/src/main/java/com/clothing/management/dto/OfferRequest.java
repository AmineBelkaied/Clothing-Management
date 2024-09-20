package com.clothing.management.dto;

import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;

import java.util.Set;
import java.util.stream.Collectors;

public class OfferRequest {
    private Long id;
    private String name;
    private Set<OfferModelsDTO> offerModels;
    private Set<FbPage> fbPages;
    private Double price;
    private boolean enabled;

    public OfferRequest(){}

    public OfferRequest(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Set<FbPage> getFbPages() {
        return fbPages;
    }

    public void setFbPages(Set<FbPage> fbPages) {
        this.fbPages = fbPages;
    }
    public Set<OfferModelsDTO> getOfferModels() {
        return offerModels;
    }

    public void setOfferModels(Set<OfferModelsDTO> offerModels) {
        this.offerModels = offerModels;
    }
}
