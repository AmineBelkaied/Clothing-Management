package com.clothing.management.tenant;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;

@Configuration
public class FlywayConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${spring.datasource.username}")
    private String masterDbUser;

    @Value("${spring.datasource.password}")
    private String masterDbPassword;

    @Value("${application.datasource.host}")
    private String dataSourceHost;

    @Value("${application.datasource.params}")
    private String dataSourceParams;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    private final MasterTenantService masterTenantService;

    public FlywayConfig(@Lazy MasterTenantService masterTenantService) {
        this.masterTenantService = masterTenantService;
    }

    @Bean
    public void migrate() {
        List<MasterTenant> masterTenants = masterTenantService.findAllMasterTenants();
        if (masterTenants.isEmpty()) {
            LOG.warn("No master tenants found for migration.");
        }

        masterTenants.forEach(masterTenant -> {
            try {
                migrateTenant(masterTenant);
            } catch (Exception e) {
                LOG.error("Error during Flyway migration for tenant: {}", masterTenant.getDbName(), e);
            }
        });
    }

    private void migrateTenant(MasterTenant masterTenant) {
        DriverManagerDataSource dataSource = createDataSource(masterTenant);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();

        try {
            LOG.info("Starting migration for tenant: {}", masterTenant.getDbName());
            flyway.migrate();
            LOG.info("Migration completed for tenant: {}", masterTenant.getDbName());
        } catch (Exception e) {
            LOG.error("Error migrating tenant: {}", masterTenant.getDbName(), e);
            throw e;
        }
    }

    private DriverManagerDataSource createDataSource(MasterTenant masterTenant) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSourceHost + masterTenant.getDbName() + dataSourceParams);
        dataSource.setUsername(masterDbUser);
        dataSource.setPassword(masterDbPassword);
        return dataSource;
    }

}
