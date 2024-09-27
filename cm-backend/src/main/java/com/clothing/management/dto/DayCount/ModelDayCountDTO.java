package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class ModelDayCountDTO extends DayCountDTO {

    private Long productId;
    private Color color;
    private Size size;
    private long countReturn;

    public ModelDayCountDTO(
            Date packetDate, Long productId,
            Color color, Size size,
            long countPayed, long countProgress, long countReturn
    ) {
        super(packetDate, countPayed, countProgress);
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.countReturn = countReturn;
    }
}
