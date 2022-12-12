package com.example.service;

import com.example.config.BotConfig;
import com.example.controller.BotController;
import jakarta.ws.rs.ext.ParamConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotController botController;


   private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig, @Lazy BotController botController) {
        this.botConfig = botConfig;
        this.botController = botController;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();

            botController.handleCallBackQuery(callbackQuery);


        }

        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();

            System.out.println(user.getFirstName() + " " + user.getLastName() + " " + message.getText());

            botController.handleMessage(message);



        }

    }

    public void send(SendMessage sms) {
        try {
            execute(sms);
        } catch (TelegramApiException e){
            throw new RuntimeException("Message not send");
        }
    }
}
