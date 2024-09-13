package com.clothing.management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferModelKey implements Serializable {

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "offer_id")
    private Long offerId;
}
