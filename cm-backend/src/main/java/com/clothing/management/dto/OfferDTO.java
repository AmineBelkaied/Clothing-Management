package com.clothing.management.dto;

import com.clothing.management.entities.*;

import java.util.Set;
import java.util.stream.Collectors;

public class OfferDTO {
    private Long id;
    private String name;
    private Set<OfferModelsDTO> offerModels;
    private Set<FbPage> fbPages;
    private Double price;
    private boolean enabled;

    public OfferDTO(){}
    public OfferDTO(String name){
        this.name = name;
    }

    public OfferDTO(Offer offer){
        this.id = offer.getId();
        this.name = offer.getName();
        this.offerModels = offer.getOfferModels().stream().map(offerModels -> new OfferModelsDTO(offerModels)).collect(Collectors.toSet());
        this.fbPages = offer.getFbPages().stream().map(FbPage::new).collect(Collectors.toSet());
        this.price = offer.getPrice();
        this.enabled = offer.isEnabled();
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
