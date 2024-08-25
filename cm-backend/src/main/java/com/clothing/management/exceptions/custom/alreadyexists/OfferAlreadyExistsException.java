package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class OfferAlreadyExistsException extends AlreadyExistsException {

    public OfferAlreadyExistsException(Long offerId, String offerName) {
        super("Offer", offerId, offerName);
    }
}
