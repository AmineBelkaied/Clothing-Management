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
