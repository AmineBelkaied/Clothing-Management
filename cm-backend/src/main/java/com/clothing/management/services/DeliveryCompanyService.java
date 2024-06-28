package com.clothing.management.services;

import com.clothing.management.entities.DeliveryCompany;

import java.util.List;
import java.util.Optional;

public interface DeliveryCompanyService {

    public List<DeliveryCompany> findAllDeliveryCompanies();
    public Optional<DeliveryCompany> findDeliveryCompanyById(Long id);
    public DeliveryCompany addDeliveryCompany(DeliveryCompany deliveryCompany);
    public DeliveryCompany updateDeliveryCompany(DeliveryCompany deliveryCompany);
    public void deleteDeliveryCompany(DeliveryCompany deliveryCompany);
    public void deleteDeliveryCompanyById(Long id);
}
