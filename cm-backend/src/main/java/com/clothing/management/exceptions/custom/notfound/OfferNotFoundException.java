package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class OfferNotFoundException extends EntityNotFoundException {

    public OfferNotFoundException(Long offerId) {
        super("Offer", offerId);
    }

    public OfferNotFoundException(Long offerId, String offerName) {
        super("Offer", offerId, offerName);
    }
}
