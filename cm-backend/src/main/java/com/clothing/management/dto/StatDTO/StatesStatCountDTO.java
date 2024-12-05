package com.clothing.management.dto.StatDTO;

import lombok.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StatesStatCountDTO extends DayCountDTO {

    private String governerateName;
    private Long countReturn;

    public StatesStatCountDTO(Date packetDate, String governerateName, Long countPaid, Long countProgress, Long countReturn) {
        super( packetDate, countPaid, countProgress);
        this.governerateName = governerateName;
        this.countReturn =countReturn;
    }
}
