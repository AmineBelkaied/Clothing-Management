package com.clothing.management.dto.DayCount;
import com.clothing.management.entities.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ModelsDayCountDTO extends DayCountDTO {
    private Date date;
    private Long packetId;
    private Model model;
    private long countReturn;
    private long countOos;
    private double profits;

    public ModelsDayCountDTO(Date packetDate,
                             Long packetId,
                             Model model,
                             long countPaid, long countProgress, long countReturn,long countOos, double profits
    ) {
        super(countPaid, countProgress);
        this.date = packetDate;
        this.packetId = packetId;
        this.model = model;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }
}
