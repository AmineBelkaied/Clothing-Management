package com.clothing.management.auth.mastertenant.repository;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MasterTenantRepository extends JpaRepository<MasterTenant, Integer> {
    MasterTenant findByTenantClientId(Integer clientId);

    MasterTenant findByTenantName(String tenantName);
}
