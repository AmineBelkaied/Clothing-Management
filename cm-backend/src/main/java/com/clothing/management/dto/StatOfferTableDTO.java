package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
