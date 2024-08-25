package com.clothing.management.exceptions.custom.notfound;

import com.clothing.management.exceptions.generic.EntityNotFoundException;

public class DeliveryCompanyNotFoundException extends EntityNotFoundException {

    public DeliveryCompanyNotFoundException(Long deliveryCompanyId, String deliveryCompanyName) {
        super("DeliveryCompany", deliveryCompanyId, deliveryCompanyName);
    }
}
