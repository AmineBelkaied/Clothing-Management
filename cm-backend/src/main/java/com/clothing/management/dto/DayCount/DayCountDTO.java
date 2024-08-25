package com.clothing.management.dto.DayCount;

import com.clothing.management.dto.OfferDTO;
import com.clothing.management.entities.Offer;

import java.util.Date;

public class DayCountDTO {
    private Date date;
    private long countProgress;
    private long countPayed;
    private long countReturn;
    private double profits;

    public DayCountDTO() {
    }

    public DayCountDTO(
            Date packetDate,
            long countPayed, long countProgress, long countReturn, double profits
    ) {
        this.date = packetDate;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
        this.countReturn = countReturn;
        this.profits = profits;
    }
    public DayCountDTO(
            Date date,
            long countPayed, long countProgress, long countReturn
    ) {
        this.date = date;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
        this.countReturn = countReturn;
        this.profits = profits;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date ate) {
        this.date = date;
    }

    public long getCountProgress() {
        return countProgress;
    }
    public void setCountProgress(long countProgress) {
        this.countProgress = countProgress;
    }

    public long getCountPayed() {
        return countPayed;
    }

    public void setCountPayed(long countPayed) {
        this.countPayed = countPayed;
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
        return "OffersDayCountDTO{" +
                "packetDate=" + date +
                ", countProgress=" + countProgress +
                ", countPayed=" + countPayed +
                ", countReturn=" + countReturn +
                ", profits=" + profits +
                '}';
    }
}