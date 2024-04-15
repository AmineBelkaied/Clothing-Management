package com.clothing.management.auth.mastertenant.service;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;

import java.util.List;

public interface MasterTenantService {

    MasterTenant save(MasterTenant masterTenant);
    List<MasterTenant> findAllMasterTenants();
    MasterTenant findByClientId(Integer clientId);
    MasterTenant findByTenantName(String tenantName);
}
