package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Product;
import com.clothing.management.entities.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldProductsDayCountDTO extends DayCountDTO {

    private Long id;

    @Builder.Default
    private long countExchange = 0;
    @Builder.Default
    private long countOos = 0;

    private Color color;
    private Size size;

    @Builder.Default
    private long qte = 0;

    public SoldProductsDayCountDTO(
            Long id,
            Color color, Size size, long qte,
            long countPayed, long countProgress, long countOos, long countExchange
    ) {
        super(countPayed, countProgress);
        this.id = id;
        this.countExchange =countExchange;
        this.countOos = countOos;
        this.color = color;
        this.size = size;
        this.qte = qte;
    }
    public SoldProductsDayCountDTO(Product product) {
        super(0, 0);
        this.id = product.getId();
        this.countExchange =0;
        this.countOos = 0;
        this.color = product.getColor();
        this.size = product.getSize();
        this.qte = product.getQuantity();
    }
}
