package com.clothing.management.dto;

import com.clothing.management.entities.FbPage;
import com.clothing.management.entities.Offer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferRequest {
    private Long id;
    private String name;
    private Set<OfferModelsDTO> offerModels;
    private Set<FbPage> fbPages;
    private Double price;
    private boolean enabled;

    public OfferRequest(String name){
        this.name = name;
    }
}
