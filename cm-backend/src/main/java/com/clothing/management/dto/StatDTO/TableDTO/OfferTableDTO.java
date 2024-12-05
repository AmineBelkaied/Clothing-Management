package com.clothing.management.dto.StatDTO.TableDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)

public class OfferTableDTO extends TableDTO {
    private Long offerId;
    private String offerName;

    public OfferTableDTO(Long offerId, String offerName,
                         long countPaid, long countProgress, long countReturn, double profits
    ) {
        super(countPaid, countProgress, countReturn, profits);
        this.offerId = offerId;
        this.offerName = offerName;
    }

}
