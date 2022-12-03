package com.clothing.management.services;

import com.clothing.management.entities.Governorate;

import java.util.List;
import java.util.Optional;

public interface GovernorateService {
    
    public List<Governorate> findAllGovernorates();
    public Optional<Governorate> findGovernorateById(Long idGovernorate);
    public Governorate addGovernorate(Governorate governorate);
    public Governorate updateGovernorate(Governorate governorate);
    public void deleteGovernorate(Governorate governorate);
    public void deleteSelectedGovernorates(List<Long> governoratesId);
}
