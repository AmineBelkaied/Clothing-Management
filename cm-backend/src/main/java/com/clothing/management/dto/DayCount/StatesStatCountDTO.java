package com.clothing.management.dto.DayCount;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
public class StatesStatCountDTO extends DayCountDTO {

    private String governerateName;
    private Long countReturn;

    public StatesStatCountDTO(Date date, String governerateName, Long countPayed, Long countProgress, Long countReturn) {
        super(date, countPayed, countProgress);
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }
}
