package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelStockValueDTO {

    private String name;
    private long quantity;
    private double purshasePrice;
    private double sellingPrice;
    private double profits;

}
