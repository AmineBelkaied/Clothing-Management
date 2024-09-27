package com.clothing.management.dto.DayCount;
import lombok.Data;

import java.util.Date;

@Data
public class DayCountDTO {

    private Date date;
    private long countProgress;
    private long countPayed;

    public DayCountDTO() {
        this.countPayed = 0;
        this.countProgress = 0;
    }

    public DayCountDTO(
            Date packetDate,
            long countPayed, long countProgress
    ) {
        this.date = packetDate;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
    }
    public DayCountDTO(
            long countPayed, long countProgress
    ) {
        this.countPayed = countPayed;
        this.countProgress = countProgress;
    }
}
