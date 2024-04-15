package com.clothing.management.tenant;

import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.repository.MasterTenantRepository;
import com.clothing.management.auth.util.DataSourceUtil;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import javax.sql.DataSource;
import java.util.*;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

    private static final long serialVersionUID = 1L;

    private Map<String, DataSource> dataSourcesMtApp = new TreeMap<>();

    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    public DataSourceBasedMultiTenantConnectionProviderImpl() {
    }

    @Override
    protected DataSource selectAnyDataSource() {
        // This method is called more than once. So check if the data source map
        // is empty. If it is then rescan master_tenant table for all tenant
        if (dataSourcesMtApp.isEmpty()) {
            List<MasterTenant> masterTenants = masterTenantRepository.findAll();
            LOG.info("selectAnyDataSource() method call...Total tenants:" + masterTenants.size());
            for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getDbName(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
               //generateSchemaForTenant(this.dataSourcesMtApp.values().iterator().next(), masterTenant.getDbName());
            }
        }
        return this.dataSourcesMtApp.values().iterator().next();
    }

   /* public void generateSchemaForTenant(DataSource dataSource, String tenantId) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.datasource", dataSource)
                .build();

        PersistenceUnitInfo persistenceUnitInfo = createPersistenceUnitInfo(tenantId);

        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(persistenceUnitInfo, getJpaProperties());

        entityManagerFactory.getProperties().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        entityManagerFactory.close();
        StandardServiceRegistryBuilder.destroy(registry);

        System.out.println("Schema for tenant '" + tenantId + "' generated or updated successfully.");
    }

    private PersistenceUnitInfo createPersistenceUnitInfo(String tenantId) {
        // Implement logic to create a PersistenceUnitInfo for the specified tenantId
        // This could involve creating a new instance of a class that implements PersistenceUnitInfo.
        // Return the configured PersistenceUnitInfo.
        // ...

        // For simplicity, you can use Persistence.createEntityManagerFactory to create a dummy unit.
        return Persistence.createEntityManagerFactory("tenantdb-persistence-unit").unwrap(PersistenceUnitInfo.class);
    }
    private Map<String, Object> getJpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.schema-generation.database.action", "update");
        properties.put("hibernate.multiTenancy", "DATABASE");
        properties.put("hibernate.tenant_identifier_resolver", "com.clothing.management.tenant.CurrentTenantIdentifierResolverImpl");

        // Add any additional JPA properties you need

        return properties;
    }*/
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        // If the requested tenant id is not present check for it in the master
        // database 'master_tenant' table
        tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            List<MasterTenant> masterTenants = masterTenantRepository.findAll();
            LOG.info("selectDataSource() method call...Tenant:" + tenantIdentifier + " Total tenants:" + masterTenants.size());
            for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getDbName(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
            }
        }
        //check again if tenant exist in map after rescan master_db, if not, throw UsernameNotFoundException
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            LOG.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in master db after rescan");
            throw new UsernameNotFoundException(String.format("Tenant not found after rescan, " + " tenant=%s", tenantIdentifier));
        }
        return this.dataSourcesMtApp.get(tenantIdentifier);
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (tenantIdentifier != DBContextHolder.getCurrentDb()) {
            tenantIdentifier = DBContextHolder.getCurrentDb();
        }
        return tenantIdentifier;
    }
}
