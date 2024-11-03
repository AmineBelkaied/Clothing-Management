package com.clothing.management.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StatOfferTableDTO extends StatTableDTO {

    @Builder.Default
    private Double purchasePrice= 0.0;
    @Builder.Default
    private Double sellingPrice= 0.0;

}
