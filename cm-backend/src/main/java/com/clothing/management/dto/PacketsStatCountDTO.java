package com.clothing.management.dto;

import java.util.Date;

public class PacketsStatCountDTO {
    private Date date;
    private Long countPayed;
    private Long countOut;
    private Long countExchange;
    private Long countReturn;
    private Long countOos;

    private Long countAll;

    public PacketsStatCountDTO() {
    }

    public PacketsStatCountDTO(Date date, Long countPayed, Long countOut, Long countExchange, Long countReturn, Long countOos, Long countAll) {
        this.date = date;
        this.countPayed = countPayed;
        this.countOut = countOut;
        this.countExchange = countExchange;
        this.countReturn = countReturn;
        this.countOos = countOos;
        this.countAll = countAll;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCountPayed() {
        return countPayed;
    }

    public void setCountPayed(Long countPayed) {
        this.countPayed = countPayed;
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

    public Long getCountReturn() {
        return countReturn;
    }

    public void setCountReturn(Long countReturn) {
        this.countReturn = countReturn;
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

    @Override
    public String toString() {
        return "PacketsStatCountDTO{" +
                "date=" + date +
                ", countPayed=" + countPayed +
                ", countOut=" + countOut +
                ", countExchange=" + countExchange +
                ", countReturn=" + countReturn +
                ", countEnded=" + countOos +
                ", countAll=" + countAll +
                '}';
    }
}
