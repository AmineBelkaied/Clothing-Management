package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Offer;

import java.util.Date;

public class OffersDayCountDTO  extends DayCountDTO {

    private Long packetId;
    private Long packetOfferId;
    private Offer offer;
    private long countReturn;
    private double profits;

    public OffersDayCountDTO() {
    }

    public OffersDayCountDTO( Date packetDate,
                              Long packetId,
                              Offer offer,
                              Long packetOfferId,
                              long countPayed, long countProgress, long countReturn, double profits
    ) {
        super(packetDate, countPayed, countProgress);
        this.packetId = packetId;
        this.offer = offer;
        this.packetOfferId = packetOfferId;
        this.countReturn = countReturn;
        this.profits = profits;

    }

    public Long getPacketId() {
        return packetId;
    }

    public void setPacketId(Long packetId) {
        this.packetId = packetId;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Long getPacketOfferId() {
        return packetOfferId;
    }

    public void setPacketOfferId(Long packetOfferId) {
        this.packetOfferId = packetOfferId;
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
                "packetId=" + packetId +
                ", offer=" + offer +
                ", packetOfferId=" + packetOfferId +
                '}';
    }
}
