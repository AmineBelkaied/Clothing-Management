package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Governorate;
import com.clothing.management.repository.IGovernorateRepository;
import com.clothing.management.services.GovernorateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GovernorateServiceImpl implements GovernorateService {

    @Autowired
    IGovernorateRepository governorateRepository;

    @Override
    public List<Governorate> findAllGovernorates() {
        return governorateRepository.findAll();
    }

    @Override
    public Optional<Governorate> findGovernorateById(Long idGovernorate) {
        return governorateRepository.findById(idGovernorate);
    }

    @Override
    public Governorate addGovernorate(Governorate governorate) {
        return governorateRepository.save(governorate);
    }

    @Override
    public Governorate updateGovernorate(Governorate governorate) {
        return governorateRepository.save(governorate);
    }

    @Override
    public void deleteGovernorate(Governorate governorate) {
        governorateRepository.delete(governorate);
    }

    @Override
    public void deleteSelectedGovernorates(List<Long> governoratesId) { governorateRepository.deleteAllById(governoratesId); }

    @Override
    public void deleteGovernorateById(Long id) {
        governorateRepository.deleteById(id);
    }
}
