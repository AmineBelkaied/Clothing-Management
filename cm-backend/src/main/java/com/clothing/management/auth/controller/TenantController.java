package com.clothing.management.auth.controller;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.tenant.TenantDatabaseCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/tenant")
@CrossOrigin
public class TenantController {

    @Autowired
    TenantDatabaseCreator tenantDatabaseCreator;

    @Autowired
    MasterTenantService masterTenantService;

    @GetMapping(path = "/add/{tenantName}")
    public void addTenant(@PathVariable String tenantName) throws SQLException, IOException, URISyntaxException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        tenantDatabaseCreator.createTenantDb(tenantName);
    }

    @GetMapping(path = "/findAll")
    public List<MasterTenant> findAllTenants() {
        return masterTenantService.findAllMasterTenants();
    }
}
