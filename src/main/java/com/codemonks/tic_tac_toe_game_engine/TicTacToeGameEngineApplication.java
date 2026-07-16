package com.codemonks.tic_tac_toe_game_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {
		"com.codemonks.tic_tac_toe_game_engine",
		"com.codemonks.ludo_engine"
})
public class TicTacToeGameEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicTacToeGameEngineApplication.class, args);
	}

}
