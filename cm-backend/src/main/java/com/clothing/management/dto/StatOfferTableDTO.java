package com.clothing.management.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StatOfferTableDTO extends StatTableDTO {

    private OfferDTO offer;
    private Double purchasePrice;
    private Double sellingPrice;

    public StatOfferTableDTO(OfferDTO offer) {
        super(offer.getName());
        this.offer = offer;
        this.purchasePrice = 0.0;
        this.sellingPrice = 0.0;
    }
}
