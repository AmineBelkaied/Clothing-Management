package com.clothing.management.dto.DayCount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacketsStatCountDTO extends DayCountDTO {

    private Long countOut;
    private Long countExchange;
    private Long countOos;
    private Long countAll;
    private long countReturn;
    private double profits;
}
