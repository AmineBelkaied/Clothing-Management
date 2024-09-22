package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedProductsDTO {

    private Long idPacket;
    private double totalPrice;
    private double deliveryPrice;
    private double discount;
    private String packetDescription;
    private List<ProductOfferDTO> productsOffers;
    private String status;
    private Integer productCount;
}

