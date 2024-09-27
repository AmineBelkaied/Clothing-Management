package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Governorate;
import com.clothing.management.repository.IGovernorateRepository;
import com.clothing.management.services.GovernorateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GovernorateServiceImpl implements GovernorateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GovernorateServiceImpl.class);

    private final IGovernorateRepository governorateRepository;

    @Autowired
    public GovernorateServiceImpl(IGovernorateRepository governorateRepository) {
        this.governorateRepository = governorateRepository;
    }

    @Override
    public List<Governorate> findAllGovernorates() {
        List<Governorate> governorates = governorateRepository.findAll();
        LOGGER.info("Found {} governorates.", governorates.size());
        return governorates;
    }

    @Override
    public Optional<Governorate> findGovernorateById(Long idGovernorate) {
        Optional<Governorate> governorate = governorateRepository.findById(idGovernorate);
        if (governorate.isPresent()) {
            LOGGER.info("Governorate found: {}", governorate.get());
        } else {
            LOGGER.warn("Governorate with ID: {} not found.", idGovernorate);
        }
        return governorate;
    }

    @Override
    public Governorate addGovernorate(Governorate governorate) {
        Governorate savedGovernorate = governorateRepository.save(governorate);
        LOGGER.info("Governorate added with ID: {}", savedGovernorate.getId());
        return savedGovernorate;
    }

    @Override
    public Governorate updateGovernorate(Governorate governorate) {
        Governorate updatedGovernorate = governorateRepository.save(governorate);
        LOGGER.info("Governorate updated with ID: {}", updatedGovernorate.getId());
        return updatedGovernorate;
    }

    @Override
    public void deleteGovernorate(Governorate governorate) {
        governorateRepository.delete(governorate);
        LOGGER.info("Governorate deleted.");
    }

    @Override
    public void deleteSelectedGovernorates(List<Long> governoratesId) {
        governorateRepository.deleteAllById(governoratesId);
        LOGGER.info("Governorates deleted.");
    }

    @Override
    public void deleteGovernorateById(Long id) {
        governorateRepository.deleteById(id);
        LOGGER.info("Governorate with ID: {} deleted.", id);
    }
}
