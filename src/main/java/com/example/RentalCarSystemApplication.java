package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDate;

@SpringBootApplication
public class RentalCarSystemApplication {

	public static void main(String[] args) {

		System.out.println(LocalDate.now());

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
