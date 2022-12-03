package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class OfferModel {

    @EmbeddedId
    @JsonIgnore
    OfferModelKey id;

    @ManyToOne
    @MapsId("offerId")
    @JoinColumn(name = "offer_id")
    @JsonIgnore
    Offer offer;

    @ManyToOne
    @MapsId("modelId")
    @JoinColumn(name = "model_id")
    Model model;

    Integer quantity;

    public OfferModel() {
    }

    public OfferModel(OfferModelKey id, Offer offer, Model model, Integer quantity) {
        this.id = id;
        this.offer = offer;
        this.model = model;
        this.quantity = quantity;
    }

    public OfferModel(Offer offer, Model model, Integer quantity) {
        this.offer = offer;
        this.model = model;
        this.quantity = quantity;
    }

    public OfferModelKey getId() {
        return id;
    }

    public void setId(OfferModelKey id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OfferModel{" +
                "id=" + id +
                ", offer=" + offer +
                ", model=" + model +
                ", quantity=" + quantity +
                '}';
    }
}
