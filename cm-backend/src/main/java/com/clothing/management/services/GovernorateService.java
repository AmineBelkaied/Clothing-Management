package com.clothing.management.services;

import com.clothing.management.entities.Governorate;

import java.util.List;
import java.util.Optional;

public interface GovernorateService {
    
    List<Governorate> findAllGovernorates();
    Optional<Governorate> findGovernorateById(Long idGovernorate);
    Governorate addGovernorate(Governorate governorate);
    Governorate updateGovernorate(Governorate governorate);
    void deleteGovernorate(Governorate governorate);
    void deleteSelectedGovernorates(List<Long> governoratesId);
    void deleteGovernorateById(Long id);
}
