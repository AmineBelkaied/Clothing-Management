package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StatModelTableDTO extends StatTableDTO {
    private Double purchasePrice;
    private Double sellingPrice;

    public StatModelTableDTO(ModelDTO model) {
        super(model.getName());
        this.purchasePrice = 0.0;
        this.sellingPrice = 0.0;
    }
}
