package com.clothing.management.dto.DayCount;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PacketsStatCountDTO extends DayCountDTO {

    private Date date;
    private Long countOut;
    private Long countExchange;
    private Long countOos;
    private Long countAll;
    private long countReturn;

    public PacketsStatCountDTO(Date date, Long countPayed, Long countOut,
                               Long countExchange, Long countReturn,
                               Long countOos, Long countProgress, Long countAll) {
        super(countPayed, countProgress);
        this.date = date;
        this.countOut = countOut;
        this.countExchange = countExchange;
        this.countOos = countOos;
        this.countAll = countAll;
        this.countReturn =countReturn;
    }
}
