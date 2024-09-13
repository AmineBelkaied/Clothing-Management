package com.clothing.management.dto;

import com.clothing.management.entities.*;
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
    private String  name;
    private String description;
    private float purchasePrice;
    private double earningCoefficient;
    private boolean deleted;
    @Builder.Default
    private List<Long> colors = new ArrayList<>();
    @Builder.Default
    private List<Long> sizes = new ArrayList<>();
    private boolean enabled;
    private Long defaultId;
}
