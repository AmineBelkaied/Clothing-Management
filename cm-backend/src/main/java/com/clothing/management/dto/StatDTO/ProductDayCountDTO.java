package com.clothing.management.dto.StatDTO;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductDayCountDTO extends DayCountDTO {

    private Long productId;
    private Color color;
    private Size size;
    private long countReturn;

    public ProductDayCountDTO(
            Date packetDate, Long productId,
            Color color, Size size,
            long countPaid, long countProgress, long countReturn
    ) {
        super( packetDate, countPaid, countProgress);
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.countReturn = countReturn;
    }
}
