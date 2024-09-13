package com.clothing.management.dto;

import com.clothing.management.entities.Model;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import com.clothing.management.entities.OfferModelKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

public class OfferModelsDTO {
    ModelDTO model;
    Integer quantity;


    public OfferModelsDTO() {
    }

    public OfferModelsDTO(OfferModel offerModel) {
        this.model = new ModelDTO(offerModel.getModel(),true);
        this.quantity = offerModel.getQuantity();
    }

    public ModelDTO getModel() {
        return model;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
