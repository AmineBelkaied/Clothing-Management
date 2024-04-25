package com.clothing.management;

import com.clothing.management.tenant.TenantDatabaseManager;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ClothingMangementApplication {

	private final TenantDatabaseManager tenantDatabaseManager;

	public ClothingMangementApplication(TenantDatabaseManager tenantDatabaseManager) {
		this.tenantDatabaseManager = tenantDatabaseManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(ClothingMangementApplication.class, args);
	}

	@PostConstruct
	public void updateAllTenantDatabases() {
		tenantDatabaseManager.updateAllTenantDatabases();
	}
}
