package com.codemonks.ludo_game_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LudoGameEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(LudoGameEngineApplication.class, args);
	}

}
