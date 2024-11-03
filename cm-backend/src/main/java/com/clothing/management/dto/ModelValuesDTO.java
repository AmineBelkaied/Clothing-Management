package com.clothing.management.dto;

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
public class ModelValuesDTO {

    private Long id;
    private String name;
    private Long quantity;
    private float purchasePrice;
    private double earningCoefficient;
    private boolean deleted;
    private boolean enabled;


}
