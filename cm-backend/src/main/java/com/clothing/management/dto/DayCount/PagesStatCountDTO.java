package com.clothing.management.dto.DayCount;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class PagesStatCountDTO extends DayCountDTO {

    private String pageName;
    private Long countReturn;

    public PagesStatCountDTO(Date date, String pageName, Long countPayed, Long countProgress, Long countReturn) {
        super(date, countPayed, countProgress);
        this.pageName = pageName;
        this.countReturn =countReturn;
    }
}
