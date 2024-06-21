package com.clothing.management.dto;

import com.clothing.management.entities.Model;

public class StatTableDTO {

    private String name;

    private Integer Min;

    private Integer Max;

    private Long Avg;

    private Long Sum;

    private double Per;

    private Long Retour;

    public StatTableDTO(String name) {
        this.name = name;
        this.Min = 1000;
        this.Max = 0;
        this.Avg = 0L;
        this.Per = 0L;
        this.Retour = 0L;
    }
    public StatTableDTO(String name, Integer min, Integer max, Long avg, Long sum, Long retour) {
        this.name = name;
        this.Min = min;
        this.Max = max;
        this.Avg = avg;
        this.Sum = sum;
        this.Retour = retour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMin() {
        return Min;
    }

    public void setMin(Integer min) {
        Min = min;
    }
    public void setMin(Long min) {
        Min = Math.toIntExact(min);
    }

    public Integer getMax() {
        return Max;
    }

    public void setMax(Integer max) {
        Max = max;
    }

    public void setMax(Long max) {
        Max = Math.toIntExact(max);
    }

    public Long getAvg() {
        return Avg;
    }

    public void setAvg(Long avg) {
        Avg = avg;
    }

    public Long getSum() {
        if(Sum == null)return 0L;
        return Sum;
    }

    public void setSum(Long sum) {
        Sum = sum;
    }

    public double getPer() {
        return Per;
    }

    public void setPer(double per) {
        Per = per;
    }

    public Long getRetour() {
        return Retour;
    }

    public void setRetour(Long retour) {
        Retour = retour;
    }

    @Override
    public String toString() {
        return "StatTableDTO{" +
                "name='" + name + '\'' +
                ", Min=" + Min +
                ", Max=" + Max +
                ", Avg=" + Avg +
                ", Sum=" + Sum +
                ", Per=" + Per +
                ", Retour=" + Retour +
                '}';
    }
}
