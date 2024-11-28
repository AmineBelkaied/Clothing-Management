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
public class ProductsPacketDTO {

    long id;
    long packetOfferId;
    List<Long> productIds;
    List<ProductResponse> products;

}
