package com.clothing.management.dto.StatDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayCountDTO {
    private Date date;
    private long countPaid;
    private long countProgress;
}
