package com.clothing.management.repository;

import com.clothing.management.entities.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDeliveryCompanyRepository extends JpaRepository<DeliveryCompany, Long > {
}
