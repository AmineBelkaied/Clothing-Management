package com.clothing.management.dto.DayCount;
import com.clothing.management.entities.FbPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PagesDayCountDTO extends DayCountDTO {
    private Long packetId;
    private FbPage fbPage;
    private long countReturn;
    private long countOos;
    private double profits;

    public PagesDayCountDTO(Date packetDate,
                            Long packetId,
                            FbPage fbPage,
                            long countPaid, long countProgress, long countReturn, long countOos, double profits
    ) {
        super( packetDate, countPaid, countProgress);
        this.packetId = packetId;
        this.fbPage = fbPage;
        this.countOos = countOos;
        this.countReturn = countReturn;
        this.profits = profits;
    }
}
