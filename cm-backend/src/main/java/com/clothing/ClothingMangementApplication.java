package com.clothing;

import com.clothing.management.entities.GlobalConf;
import com.clothing.management.repository.IGlobalConfRepository;
import com.clothing.management.servicesImpl.PacketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;


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
