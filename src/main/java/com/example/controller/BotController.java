package com.example.controller;

import com.example.enums.ProfileStep;
import com.example.service.ProfileService;
import com.example.service.TelegramBot;
import com.example.service.UnsavedProfileService;
import jakarta.ws.rs.ext.ParamConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;

@Controller
public class BotController {

    @Autowired
    private UnsavedProfileService unsavedProfileService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TelegramBot bot;



    public void handleMessage(Message message){
        String text = message.getText();
        Long tgId = message.getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        if (text.equals("start") || text.equals("/start")) {

            boolean t = profileService.getByTgId(tgId);
            if (!t){
                unsavedProfileService.create(tgId);
                sendMessage.setText(" Enter phone ");

                bot.send(sendMessage);
            }

        } else if(unsavedProfileService.getByTgId(tgId) != null){

            ProfileStep step = unsavedProfileService.getProfileStepByTgId(tgId);

            if (step.equals(ProfileStep.NAME)) {

                unsavedProfileService.updateName(text, tgId);
                sendMessage.setText("Enter surname ");
                bot.send(sendMessage);

            } else if (step.equals(ProfileStep.SURNAME)) {

                unsavedProfileService.updateSurname(text, tgId);
                profileService.create(tgId);
                sendMainMenu(message.getChatId(), "User menu");


            } else if (step.equals(ProfileStep.PHONE)){

                unsavedProfileService.updatePhone(text, tgId);
                sendMessage.setText("Enter name ");
                bot.send(sendMessage);

            } else if (step.equals(ProfileStep.CAR_DETAIL)) {

                sendMessage.setText("Hali bu tayyor emas ");
                bot.send(sendMessage);

            }

        }


//        if (profileService.getByTgId(message.getChatId())) {
//            sendMainMenu(message.getChatId(), "User menu");
//        }






    }

    public void handleCallBackQuery(CallbackQuery callbackQuery){

        String text = callbackQuery.getData();
        User user = callbackQuery.getFrom();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId().toString());

        String [] arr = text.split("/");

        switch (arr[1]){

            case "chooseCar" : {
                /// bazaga bog'lanish
                break;
            }

            case "addCar" : {
                unsavedProfileService.updateStep(ProfileStep.CAR_DETAIL, user.getId());

                sendMessage.setText("Avtomobil haqidagi barcha ma'lumotlarni kiriting");
                bot.send(sendMessage);
            }

            case "orderHistory" : {

            }


        }


    }

    public void sendMainMenu(Long chatId, String text){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(mainMenu());

        bot.send(sendMessage);
    }

    public InlineKeyboardMarkup mainMenu(){

        InlineKeyboardButton button1 = createButton("Avtomobil tanlash","/chooseCar");
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        row1.add(button1);

        InlineKeyboardButton button2 = createButton("Avtomobil qo'shish","/addCar");
        List<InlineKeyboardButton> row2 = new LinkedList<>();
        row2.add(button2);

        InlineKeyboardButton button3 = createButton("Avtomobil statusini o'zgartirish","/changeStatus");
        List<InlineKeyboardButton> row3 = new LinkedList<>();
        row3.add(button3);

        InlineKeyboardButton button4 = createButton("To'lovlar tarixi","/paymentHistory");
        List<InlineKeyboardButton> row4 = new LinkedList<>();
        row4.add(button4);

        InlineKeyboardButton button5 = createButton("Buyurtmalar tarixi","/orderHistory");
        List<InlineKeyboardButton> row5 = new LinkedList<>();
        row5.add(button5);

        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    public InlineKeyboardButton createButton(String text,String data){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(data);
        return button;
    }


}
