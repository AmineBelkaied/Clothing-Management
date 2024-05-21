package com.clothing.management.tenant;

import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clothing.management.auth.constant.AppConstants.*;

@Service
public class TenantDatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(TenantDatabaseManager.class);
    @Value("${spring.datasource.username}")
    private String masterDbUser;
    @Value("${spring.datasource.password}")
    private String masterDbPassword;
    @Value("${spring.datasource.url}")
    private String masterDbUrl;
    @Value("${application.datasource.host}")
    private String dataSourceHost;
    @Value("${application.datasource.params}")
    private String dataSourceParams;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;
    private final MasterTenantService masterTenantService;

    public TenantDatabaseManager(MasterTenantService masterTenantService) {
        this.masterTenantService = masterTenantService;
    }

    public void updateAllTenantDatabases() {
        List<MasterTenant> masterTenants = masterTenantService.findAllMasterTenants();
        try {
            for (MasterTenant masterTenant : masterTenants) {
                LOG.info("--- STARTING UPDATING DATABASE --- " + masterTenant.getDbName() + " FOR TENANT >> " + masterTenant.getTenantName());

                DriverManagerDataSource dataSource = getDriverManagerDataSource(masterTenant);
                getEntityManagerFactoryBean(masterTenant, dataSource);

                LOG.info("--- END OF UPDATING DATABASE --- " + masterTenant.getDbName() + " FOR TENANT >> " + masterTenant.getTenantName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DriverManagerDataSource getDriverManagerDataSource(MasterTenant masterTenant) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setSchema(masterTenant.getDbName());
        dataSource.setUrl(dataSourceHost + masterTenant.getDbName() + dataSourceParams);
        dataSource.setUsername(masterDbUser);
        dataSource.setPassword(masterDbPassword);
        return dataSource;
    }

    private void getEntityManagerFactoryBean(MasterTenant masterTenant, DriverManagerDataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource);
        emfBean.setPackagesToScan(APPLICATION_PACKAGE_TO_SCAN);
        emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        Map<String, Object> properties = getProperties(masterTenant);
        emfBean.setJpaPropertyMap(properties);

        emfBean.setPersistenceUnitName(dataSource.toString());
        emfBean.afterPropertiesSet();
    }

    private Map<String, Object> getProperties(MasterTenant masterTenant) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.DIALECT, hibernateDialect);
        properties.put(Environment.SHOW_SQL, true);
        properties.put(Environment.FORMAT_SQL, true);
        properties.put(Environment.HBM2DDL_AUTO, UPDATE);
        properties.put(Environment.DEFAULT_SCHEMA, masterTenant.getDbName());
        return properties;
    }

    public void createTenantDb(String tenantName) throws SQLException, IOException, ClassNotFoundException, URISyntaxException {
        //Registering the Driver
        //Getting the connection
        Class.forName(driverClassName);
        Connection con = DriverManager.getConnection(masterDbUser, masterDbUrl, masterDbPassword);
        LOG.info("Connection established......");

        //Initialize the script runner
        ScriptRunner sr = new ScriptRunner(con);
        URL res = getClass().getClassLoader().getResource(SETUP_DB_FILE_NAME);
        File file = Paths.get(res.toURI()).toFile();
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(Paths.get(res.toURI())), charset);
        content = content.replaceAll(TENANT, tenantName);
        Files.write(Paths.get(res.toURI()), content.getBytes(charset));
        //Creating a reader object
        Reader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        //Running the script
        sr.runScript(reader);

        LOG.info("-- Database created and initialized with success --");
        content = content.replaceAll(tenantName, TENANT);
        Files.write(Paths.get(res.toURI()), content.getBytes(charset));
        addMasterTenant(tenantName);
    }

    private void addMasterTenant(String tenantName) {
        MasterTenant masterTenant = new MasterTenant();
        masterTenant.setDbName(APPLICATION_NAME + "-" + tenantName);
        masterTenant.setUserName(masterDbUser);
        masterTenant.setPassword(masterDbPassword);
        masterTenant.setUrl(dataSourceHost + APPLICATION_NAME + "-" + tenantName);
        masterTenant.setDriverClass(driverClassName);
        masterTenant.setTenantName(tenantName);
        masterTenant.setStatus(String.valueOf(STATUS_SUCCESS));
        masterTenantService.save(masterTenant);
    }
}
