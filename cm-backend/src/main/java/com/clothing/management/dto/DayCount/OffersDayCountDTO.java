package com.clothing.management.dto.DayCount;

import com.clothing.management.entities.Offer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffersDayCountDTO  extends DayCountDTO {

    private Long packetId;
    private Long packetOfferId;
    private Offer offer;
    private long countReturn;
    private double profits;
}
