package com.clothing.management.dto.DayCount;

import com.clothing.management.dto.OfferDTO;
import com.clothing.management.entities.Offer;

import java.util.Date;

public class DayCountDTO {
    private Date date;
    private long countProgress;
    private long countPayed;


    public DayCountDTO() {
        this.countPayed = 0;
        this.countProgress = 0;
    }

    public DayCountDTO(
            Date packetDate,
            long countPayed, long countProgress
    ) {
        this.date = packetDate;
        this.countPayed = countPayed;
        this.countProgress = countProgress;
    }
    public DayCountDTO(
            long countPayed, long countProgress
    ) {
        this.countPayed = countPayed;
        this.countProgress = countProgress;
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


    @Override
    public String toString() {
        return "OffersDayCountDTO{" +
                "packetDate=" + date +
                ", countProgress=" + countProgress +
                ", countPayed=" + countPayed +
                '}';
    }
}
