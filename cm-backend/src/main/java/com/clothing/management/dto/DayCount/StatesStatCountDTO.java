package com.clothing.management.dto.DayCount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatesStatCountDTO extends DayCountDTO {
    private String governerateName;
    private Long countReturn;

    public StatesStatCountDTO(Date date, String governerateName, Long countPayed, Long countProgress, Long countReturn) {
        super(date, countPayed, countProgress);
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }
}
