package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "offer_model")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferModel that = (OfferModel) o;
        return id.equals(that.id) && offer.equals(that.offer) && model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offer, model);
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
