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

    public StatesStatCountDTO(Date date, String governerateName, Long countPayed, Long countProgress, Long countReturn) {
        super(countPayed, countProgress);
        this.date = date;
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }
}
