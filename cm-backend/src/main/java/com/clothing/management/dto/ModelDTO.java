package com.clothing.management.dto;

import com.clothing.management.entities.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelDTO {

    private Long id;
    private String name;
    private String description;
    private float purchasePrice;
    private double earningCoefficient;
    @Builder.Default
    private boolean deleted = false;
    @Builder.Default
    private List<Long> colors = new ArrayList<>();
    @Builder.Default
    private List<Long> sizes = new ArrayList<>();
    @Builder.Default
    private boolean enabled = false;
    private Long defaultId;
}
