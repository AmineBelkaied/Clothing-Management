package com.clothing.management.dto.DayCount;

import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PagesStatCountDTO extends DayCountDTO {

    private Date date;
    private String pageName;
    private Long countReturn;

    public PagesStatCountDTO(Date date, String pageName, Long countPaid, Long countProgress, Long countReturn) {
        super( countPaid, countProgress);
        this.date = date;
        this.pageName = pageName;
        this.countReturn =countReturn;
    }
}
