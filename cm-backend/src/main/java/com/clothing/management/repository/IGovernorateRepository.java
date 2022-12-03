package com.clothing.management.repository;

import com.clothing.management.entities.Governorate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGovernorateRepository extends JpaRepository<Governorate, Long > {
}
