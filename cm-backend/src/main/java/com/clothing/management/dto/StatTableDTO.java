package com.clothing.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StatTableDTO {

    private String name;

    private Long Min;

    private Long Max;

    private Long Avg;

    private Long Payed;

    private double Per;

    private Long Retour;

    private Long Progress;

    private Double profits;

    public StatTableDTO(String name) {
        this.name = name;
        this.Min = 1000L;
        this.Max = 0L;
        this.Avg = 0L;
        this.Per = 0L;
        this.Retour = 0L;
        this.Progress = 0L;
        this.profits = 0.0;
        this.Payed = 0L;
    }
}
