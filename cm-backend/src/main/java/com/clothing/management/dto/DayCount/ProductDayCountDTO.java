package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductDayCountDTO extends DayCountDTO {

    private Long productId;
    private Date date;
    private Color color;
    private Size size;
    private long countReturn;

    public ProductDayCountDTO(
            Date packetDate, Long productId,
            Color color, Size size,
            long countPaid, long countProgress, long countReturn
    ) {
        super(countPaid, countProgress);
        this.date = packetDate;
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.countReturn = countReturn;
    }
}
