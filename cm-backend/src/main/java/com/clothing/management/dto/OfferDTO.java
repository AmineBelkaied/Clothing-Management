package com.clothing.management.dto;

import com.clothing.management.entities.*;
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
public class OfferDTO {

    private Long id;
    private String name;
    @Builder.Default
    private Set<OfferModelsDTO> offerModels = new java.util.HashSet<>();
    @Builder.Default
    private Set<Long> fbPages= new java.util.HashSet<>();
    private Double price;
    @Builder.Default
    private boolean enabled= true;
    @Builder.Default
    private boolean deleted=false;
}
