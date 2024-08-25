package com.clothing.management.dto.DayCount;

import java.util.Date;

public class PacketsStatCountDTO extends DayCountDTO {
    private Long countOut;
    private Long countExchange;
    private Long countOos;
    private Long countAll;
    private long countReturn;
    private double profits;

    public PacketsStatCountDTO() {
    }

    public PacketsStatCountDTO(Date date, Long countPayed, Long countOut, Long countExchange, Long countReturn, Long countOos, Long countProgress, Long countAll) {
        super(date, countPayed, countProgress);
        this.countOut = countOut;
        this.countExchange = countExchange;
        this.countOos = countOos;
        this.countAll = countAll;
        this.countReturn =countReturn;
    }

    public Long getCountOut() {
        return countOut;
    }

    public void setCountOut(Long countOut) {
        this.countOut = countOut;
    }

    public Long getCountExchange() {
        return countExchange;
    }

    public void setCountExchange(Long countExchange) {
        this.countExchange = countExchange;
    }

    public Long getCountOos() {
        return countOos;
    }

    public void setCountOos(Long countOos) {
        this.countOos = countOos;
    }


    public Long getCountAll() {
        return countAll;
    }

    public void setCountAll(Long countAll) {
        this.countAll = countAll;
    }

    public long getCountReturn() {
        return countReturn;
    }

    public void setCountReturn(long countReturn) {
        this.countReturn = countReturn;
    }

    public double getProfits() {
        return profits;
    }

    public void setProfits(double profits) {
        this.profits = profits;
    }

    @Override
    public String toString() {
        return "PacketsStatCountDTO{" +
                ", countOut=" + countOut +
                ", countExchange=" + countExchange +
                ", countEnded=" + countOos +
                ", countAll=" + countAll +
                '}';
    }
}
