package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Data
@NoArgsConstructor
public class SoldProductsDayCountDTO extends DayCountDTO {

    private Long id;
    private long countExchange = 0;
    private long countOos = 0;
    private Color color;
    private Size size;
    private long qte = 0;

    public SoldProductsDayCountDTO(
            Long id,
            Color color, Size size, long qte,
            long countPayed, long countProgress, long countOos, long countExchange
    ) {
        super(countPayed, countProgress);
        this.id = id;
        this.countExchange = countExchange;
        this.countOos = countOos;
        this.color = color;
        this.size = size;
        this.qte = qte;
    }
}
