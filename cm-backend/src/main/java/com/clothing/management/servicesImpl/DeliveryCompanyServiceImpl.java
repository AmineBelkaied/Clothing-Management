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

    private final IDeliveryCompanyRepository steLivraisonRepository;

    @Autowired
    public DeliveryCompanyServiceImpl(IDeliveryCompanyRepository steLivraisonRepository) {
        this.steLivraisonRepository = steLivraisonRepository;
    }

    @Override
    public List<DeliveryCompany> findAllStesLivraison() {
        return steLivraisonRepository.findAll();
    }

    @Override
    public Optional<DeliveryCompany> findSteById(Long idSte) {
        return steLivraisonRepository.findById(idSte);
    }

    @Override
    public DeliveryCompany addSte(DeliveryCompany ste) {
        return steLivraisonRepository.save(ste);
    }

    @Override
    public DeliveryCompany updateSte(DeliveryCompany ste) {
        return steLivraisonRepository.save(ste);
    }

    @Override
    public void deleteSte(DeliveryCompany ste) {
        steLivraisonRepository.delete(ste);
    }

    @Override
    public void deleteSteById(Long idSte) {
        steLivraisonRepository.deleteById(idSte);
    }
}
