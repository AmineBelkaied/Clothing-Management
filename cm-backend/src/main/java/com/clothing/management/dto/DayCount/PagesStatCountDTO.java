package com.clothing.management.dto.DayCount;

import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PagesStatCountDTO extends DayCountDTO {

    private String pageName;
    private Long countReturn;

    public PagesStatCountDTO(Date packetDate, String pageName, Long countPaid, Long countProgress, Long countReturn) {
        super( packetDate, countPaid, countProgress);
        this.pageName = pageName;
        this.countReturn =countReturn;
    }
}
