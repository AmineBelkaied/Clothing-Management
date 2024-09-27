package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Offer;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class OffersDayCountDTO  extends DayCountDTO {

    private Long packetId;
    private Long packetOfferId;
    private Offer offer;
    private long countReturn;
    private double profits;

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
}
