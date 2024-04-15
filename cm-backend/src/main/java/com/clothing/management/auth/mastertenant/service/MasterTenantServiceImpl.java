package com.clothing.management.auth.mastertenant.service;

import com.clothing.management.auth.mastertenant.repository.MasterTenantRepository;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterTenantServiceImpl implements MasterTenantService{

    private static final Logger LOG = LoggerFactory.getLogger(MasterTenantServiceImpl.class);

    @Autowired
    MasterTenantRepository masterTenantRepository;

    @Override
    public MasterTenant save(MasterTenant masterTenant) {
        return masterTenantRepository.save(masterTenant);
    }

    @Override
    public List<MasterTenant> findAllMasterTenants() {
        return masterTenantRepository.findAll();
    }

    @Override
    public MasterTenant findByClientId(Integer clientId) {
        LOG.info("findByClientId() method call...");
        return masterTenantRepository.findByTenantClientId(clientId);
    }

    @Override
    public MasterTenant findByTenantName(String tenantName) {
        LOG.info("findByUserName() method call...");
        return masterTenantRepository.findByTenantName(tenantName);
    }
}
