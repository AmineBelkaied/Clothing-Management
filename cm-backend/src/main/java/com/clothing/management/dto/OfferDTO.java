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
    private Set<OfferModelsDTO> offerModels;
    private Set<Long> fbPages;
    private Double price;
    private boolean enabled;
    private boolean deleted;
}
