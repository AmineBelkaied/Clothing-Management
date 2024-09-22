package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOfferDTO {

    private Long productId;
    private Long offerId;
    private Long packetOfferIndex;
    private  double profits;
}
