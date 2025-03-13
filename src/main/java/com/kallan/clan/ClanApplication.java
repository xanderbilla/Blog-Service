package com.kallan.clan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ClanApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClanApplication.class, args);
	}

}
