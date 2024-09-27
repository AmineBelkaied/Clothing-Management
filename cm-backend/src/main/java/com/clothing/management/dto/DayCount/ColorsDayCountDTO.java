package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
public class ColorsDayCountDTO extends DayCountDTO {

    private Color color;

    public ColorsDayCountDTO(
            Date packetDate,
            Color color,
            long countPayed, long countProgress
    ) {
        super(packetDate, countPayed, countProgress);
        this.color = color;
    }
}
