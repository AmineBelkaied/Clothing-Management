package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Color;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
public class ColorsDayCountDTO extends DayCountDTO {
    private Date date;
    private Color color;

    public ColorsDayCountDTO(
            Date packetDate,
            Color color,
            long countPayed, long countProgress
    ) {
        super(countPayed, countProgress);
        this.date = packetDate;
        this.color = color;
    }
}
