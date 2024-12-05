package com.clothing.management.dto.StatDTO;
import com.clothing.management.entities.Color;
import lombok.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ColorsDayCountDTO extends DayCountDTO {
    private Color color;

    public ColorsDayCountDTO(
            Date packetDate,
            Color color,
            long countPaid, long countProgress
    ) {
        super(packetDate,countPaid, countProgress);
        this.color = color;
    }
}
