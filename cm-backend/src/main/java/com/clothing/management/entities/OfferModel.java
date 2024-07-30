package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "offer_model", indexes = {
        @Index(name = "idx_offer_id", columnList = "offer_id")
})
public class OfferModel {

    @EmbeddedId
    @JsonIgnore
    OfferModelKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("offerId")
    @JoinColumn(name = "offer_id")
    Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("modelId")
    @JoinColumn(name = "model_id")
    Model model;

    Integer quantity;

    public OfferModel() {
    }

    public OfferModel(Offer offer, Model model, Integer quantity) {
        this.id = new OfferModelKey(offer.getId(), model.getId());
        this.offer = offer;
        this.model = model;
        this.quantity = quantity;
    }

    public OfferModel(Offer offer, Model model, Integer quantity,List<Product> products) {
        this.id = new OfferModelKey(offer.getId(), model.getId());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferModel that = (OfferModel) o;
        return id.equals(that.id) && offer.equals(that.offer) && model.equals(that.model) && quantity.equals(that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offer, model,quantity);
    }

    @Override
    public String toString() {
        String qte = quantity>1?" * "+quantity:"";
        return model.getName() + qte;
    }
}
