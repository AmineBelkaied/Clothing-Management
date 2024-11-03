package com.clothing.management.services;

import com.clothing.management.entities.DeliveryCompany;

import java.util.List;
import java.util.Optional;

public interface DeliveryCompanyService {

    List<DeliveryCompany> findAllDeliveryCompanies();
    Optional<DeliveryCompany> findDeliveryCompanyById(Long id);
    DeliveryCompany addDeliveryCompany(DeliveryCompany deliveryCompany);
    DeliveryCompany updateDeliveryCompany(DeliveryCompany deliveryCompany);
    void deleteDeliveryCompany(DeliveryCompany deliveryCompany);
    void deleteDeliveryCompanyById(Long id);
    Long checkDeliveryCompanyUsage(Long id);
}
