package com.clothing.management.dto.DayCount;
import lombok.*;

@Data
@NoArgsConstructor
public class SoldProductsDayCountDTO extends DayCountDTO {

    private Long id;
    private long countExchange = 0;
    private long countOos = 0;
    private Long color;
    private Long size;
    private long qte = 0;

    public SoldProductsDayCountDTO(
            Long id,
            Long color, Long size, long qte,
            long countPayed, long countProgress, long countOos, long countExchange
    ) {
        super(countPayed, countProgress);
        this.id = id;
        this.countExchange = countExchange;
        this.countOos = countOos;
        this.color = color;
        this.size = size;
        this.qte = qte;
    }
}
