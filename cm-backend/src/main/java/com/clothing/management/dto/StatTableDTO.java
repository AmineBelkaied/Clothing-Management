package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatTableDTO {

    private String name;

    private Integer Min;

    private Integer Max;

    private Long Avg;

    private Long Payed;

    private double Per;

    private Long Retour;

    private Long Progress;

    private Double profits;

    public StatTableDTO(String name) {
        this.name = name;
        this.Min = 1000;
        this.Max = 0;
        this.Avg = 0L;
        this.Per = 0L;
        this.Retour = 0L;
        this.Progress = 0L;
        this.profits = 0.0;
    }
}
