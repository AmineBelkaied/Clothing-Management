package com.clothing.management;

import com.clothing.management.services.FilesStorageService;
import com.clothing.management.services.PacketService;
import com.clothing.management.servicesImpl.PacketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

@SpringBootApplication
@EnableScheduling
public class ClothingMangementApplication implements CommandLineRunner {

	@Resource
	FilesStorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(ClothingMangementApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(PacketServiceImpl packetService) {
		return (args) -> {
			// save a couple of customers
			//packetService.addProductsPackets();
		};
	}

	@Override
	public void run(String... arg) throws Exception {
//    storageService.deleteAll();
		storageService.init();
	}
}
