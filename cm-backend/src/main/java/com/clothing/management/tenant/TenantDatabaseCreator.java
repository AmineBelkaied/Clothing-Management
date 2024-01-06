package com.clothing.management.tenant;

import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.entities.Role;
import com.clothing.management.entities.User;
import com.clothing.management.services.UserService;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class TenantDatabaseCreator {

    @Value("${master.db.user}")
    private String mysqlUser;
    @Value("${master.db.password}")
    private String mysqlPassword;
    @Value("${master.db.url}")
    private String mysqlUrl;

    private static final Logger LOG = LoggerFactory.getLogger(TenantDatabaseCreator.class);
    @Autowired
    MasterTenantService masterTenantService;

    @Autowired
    UserService userService;

    public void createTenantDb(String tenantName) throws SQLException, IOException, ClassNotFoundException, URISyntaxException {
        //Registering the Driver
        //Getting the connection
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
        LOG.info("Connection established......");

        //Initialize the script runner
        ScriptRunner sr = new ScriptRunner(con);
        URL res = getClass().getClassLoader().getResource("setup-db.sql");
        File file = Paths.get(res.toURI()).toFile();
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(Paths.get(res.toURI())), charset);
        content = content.replaceAll("tenant", tenantName);
        Files.write(Paths.get(res.toURI()), content.getBytes(charset));
        //Creating a reader object
        Reader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        //Running the script
        sr.runScript(reader);

        LOG.info("-- Database created and initialized with success --");
        content = content.replaceAll(tenantName, "tenant");
        Files.write(Paths.get(res.toURI()), content.getBytes(charset));
        addMasterTenant(tenantName);
    }

    private void addMasterTenant(String tenantName) {
        MasterTenant masterTenant = new MasterTenant();
        masterTenant.setDbName("clothing-management-" + tenantName);
        masterTenant.setUserName(mysqlUser);
        masterTenant.setPassword(mysqlPassword);
        masterTenant.setUrl("jdbc:mysql://localhost:3306/clothing-management-" + tenantName);
        masterTenant.setDriverClass("com.mysql.cj.jdbc.Driver");
        masterTenant.setTenantName(tenantName);
        masterTenant.setStatus("200");
        masterTenantService.save(masterTenant);
    }
}
