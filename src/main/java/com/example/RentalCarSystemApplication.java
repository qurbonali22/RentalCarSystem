package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class RentalCarSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(RentalCarSystemApplication.class, args);

//		try {
//			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//			telegramBotsApi.registerBot(new RentCarBot());
//		} catch (TelegramApiException e) {
//			System.out.println("xato");
//			e.printStackTrace();
//		}


	}

}
