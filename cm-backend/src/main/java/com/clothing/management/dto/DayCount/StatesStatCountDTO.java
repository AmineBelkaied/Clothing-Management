package com.clothing.management.dto.DayCount;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StatesStatCountDTO extends DayCountDTO {

    private Date date;
    private String governerateName;
    private Long countReturn;

    public StatesStatCountDTO(Date date, String governerateName, Long countPaid, Long countProgress, Long countReturn) {
        super(countPaid, countProgress);
        this.date = date;
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }
}
