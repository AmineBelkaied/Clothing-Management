package com.clothing.management;

import com.clothing.management.tenant.TenantDatabaseManager;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ClothingManagementApplication {

	private final TenantDatabaseManager tenantDatabaseManager;

	public ClothingManagementApplication(TenantDatabaseManager tenantDatabaseManager) {
		this.tenantDatabaseManager = tenantDatabaseManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(ClothingManagementApplication.class, args);
	}

	@PostConstruct
	public void updateAllTenantDatabases() {
		tenantDatabaseManager.updateAllTenantDatabases();
	}
}
