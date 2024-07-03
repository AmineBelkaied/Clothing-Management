package com.clothing.management.dto;

public class StatTableDTO {

    private String name;

    private Integer Min;

    private Integer Max;

    private Long Avg;

    private Long Payed;

    private double Per;

    private Long Retour;

    private Long Progress;

    public StatTableDTO(String name) {
        this.name = name;
        this.Min = 1000;
        this.Max = 0;
        this.Avg = 0L;
        this.Per = 0L;
        this.Retour = 0L;
        this.Progress = 0L;
    }
    public StatTableDTO(String name, Integer min, Integer max, Long avg, Long payed, Long progress, Long retour) {
        this.name = name;
        this.Min = min;
        this.Max = max;
        this.Avg = avg;
        this.Payed = payed;
        this.Retour = retour;
        this.Progress = progress;
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

    public Long getPayed() {
        if(Payed == null)return 0L;
        return Payed;
    }

    public void setPayed(Long payed) {
        Payed = payed;
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

    public Long getProgress() {
        return Progress;
    }

    public void setProgress(Long progress) {
        Progress = progress;
    }

    @Override
    public String toString() {
        return "StatTableDTO{" +
                "name='" + name + '\'' +
                ", Min=" + Min +
                ", Max=" + Max +
                ", Avg=" + Avg +
                ", Payed=" + Payed +
                ", Per=" + Per +
                ", Progress=" + Progress +
                ", Retour=" + Retour +
                '}';
    }
}
