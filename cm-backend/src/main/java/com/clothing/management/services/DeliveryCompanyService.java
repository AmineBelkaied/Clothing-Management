package com.clothing.management.services;

import com.clothing.management.entities.DeliveryCompany;

import java.util.List;
import java.util.Optional;

public interface DeliveryCompanyService {

    public List<DeliveryCompany> findAllStesLivraison();
    public Optional<DeliveryCompany> findSteById(Long idSteLivraison);
    public DeliveryCompany addSte(DeliveryCompany deliveryCompany);
    public DeliveryCompany updateSte(DeliveryCompany deliveryCompany);
    public void deleteSte(DeliveryCompany deliveryCompany);
    public void deleteSteById(Long idSteLivraison);
}
