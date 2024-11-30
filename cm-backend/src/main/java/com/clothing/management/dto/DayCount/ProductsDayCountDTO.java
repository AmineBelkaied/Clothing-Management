package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductsDayCountDTO extends DayCountDTO {

    private Long id;
    private Long modelId;
    private String modelName;
    private Color color;
    private Size size;
    private long countExchange;
    private long countOos;

    private long countReturn;
    private double profits;

    public ProductsDayCountDTO(
            Date packetDate, Long id,
            Long modelId, String modelName,
            Color color, Size size,
            long countPaid, long countProgress, long countOos, long countReturn, double profits
    ) {
        super( packetDate, countPaid, countProgress);
        this.id = id;
        this.modelId = modelId;
        this.modelName = modelName;
        this.color = color;
        this.size = size;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }
}
