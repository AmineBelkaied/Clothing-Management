package com.clothing.management.servicesImpl;

import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.repository.IDeliveryCompanyRepository;
import com.clothing.management.services.DeliveryCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryCompanyServiceImpl implements DeliveryCompanyService {

    private final IDeliveryCompanyRepository deliveryCompanyRepository;

    @Autowired
    public DeliveryCompanyServiceImpl(IDeliveryCompanyRepository deliveryCompanyRepository) {
        this.deliveryCompanyRepository = deliveryCompanyRepository;
    }

    @Override
    public List<DeliveryCompany> findAllDeliveryCompanies() {
        return deliveryCompanyRepository.findAll();
    }

    @Override
    public Optional<DeliveryCompany> findDeliveryCompanyById(Long id) {
        return deliveryCompanyRepository.findById(id);
    }

    @Override
    public DeliveryCompany addDeliveryCompany(DeliveryCompany deliveryCompany) {
        return deliveryCompanyRepository.save(deliveryCompany);
    }

    @Override
    public DeliveryCompany updateDeliveryCompany(DeliveryCompany deliveryCompany) {
        return deliveryCompanyRepository.save(deliveryCompany);
    }

    @Override
    public void deleteDeliveryCompany(DeliveryCompany deliveryCompany) {
        deliveryCompanyRepository.delete(deliveryCompany);
    }

    @Override
    public void deleteDeliveryCompanyById(Long id) {
        deliveryCompanyRepository.deleteById(id);
    }
}
