package com.clothing.management.dto.DayCount;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PacketsStatCountDTO extends DayCountDTO {
    private Long countRecived;
    private Long countOut;
    private Long countExchange;
    private Long countOos;
    private Long countAll;
    private long countReturn;

    public PacketsStatCountDTO(Date date, Long countRecived, Long countPaid, Long countOut,
                               Long countExchange, Long countReturn,
                               Long countOos, Long countProgress, Long countAll) {
        super(date,countPaid, countProgress);
        this.countRecived = countRecived;
        this.countOut = countOut;
        this.countExchange = countExchange;
        this.countOos = countOos;
        this.countAll = countAll;
        this.countReturn =countReturn;
    }
}
