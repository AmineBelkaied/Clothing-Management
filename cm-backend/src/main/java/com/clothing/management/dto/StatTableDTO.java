package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.mapstruct.Mapping;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StatTableDTO {

    private String name;
    @Builder.Default
    private Long Min=1000L;
    @Builder.Default
    private Long Max=0L;
    @Builder.Default
    private Long Avg=0L;
    @Builder.Default
    private long payed=0L;
    @Builder.Default
    private double Per=0.0;
    @Builder.Default
    private Long retour=0L;
    @Builder.Default
    private Long progress=0L;
    @Builder.Default
    private Double profits =0.0;

    public StatTableDTO(String name) {
        this.name = name;
        this.Min = 1000L;
        this.Max = 0L;
        this.Avg = 0L;
        this.Per = 0.0;
        this.retour = 0L;
        this.progress = 0L;
        this.profits = 0.0;
        this.payed = 0L;
    }
}
