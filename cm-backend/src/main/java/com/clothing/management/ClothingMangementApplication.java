package com.clothing.management;

import com.clothing.management.servicesImpl.PacketServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ClothingMangementApplication {

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
}
