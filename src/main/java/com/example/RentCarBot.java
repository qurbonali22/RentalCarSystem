package com.example;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
public class RentCarBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "super_tester1_bot";
    }

    @Override
    public String getBotToken() {
        return "5630616270:AAHy54f7X2udvRIKJgdb18RI2LhyAp-LELM";
    }

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println(update.getMessage().getText());

    }
}
