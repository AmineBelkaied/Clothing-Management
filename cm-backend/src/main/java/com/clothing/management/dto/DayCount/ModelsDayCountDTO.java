package com.clothing.management.dto.DayCount;
import com.clothing.management.entities.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)

public class ModelsDayCountDTO extends DayCountDTO {
    private Model model;
    private long countReturn;
    private long countOos;
    private double profits;

    public ModelsDayCountDTO(Model model,
                             long countPaid, long countProgress, long countReturn,long countOos, double profits
    ) {
        super(countPaid, countProgress);
        this.model = model;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }
    public ModelsDayCountDTO(Date date,Model model,
                             long countPaid, long countProgress, long countReturn,long countOos, double profits
    ) {
        super(date,countPaid, countProgress);
        this.model = model;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }
}
