package com.clothing.management.exceptions.custom.alreadyexists;

import com.clothing.management.exceptions.generic.AlreadyExistsException;

public class DeliveryCompanyAlreadyExistsException extends AlreadyExistsException {

    public DeliveryCompanyAlreadyExistsException(Long deliveryCompanyId, String deliveryCompanyName) {
        super("DeliveryCompany", deliveryCompanyId, deliveryCompanyName);
    }
}
