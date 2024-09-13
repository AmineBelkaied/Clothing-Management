package com.clothing.management.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "offer_model", indexes = {
        @Index(name = "idx_offer_id", columnList = "offer_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
