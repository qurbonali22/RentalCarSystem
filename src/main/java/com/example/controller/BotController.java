package com.example.controller;

import com.example.dto.CarDTO;
import com.example.entity.OrderEntity;
import com.example.enums.CarStatus;
import com.example.enums.ProfileStep;
import com.example.service.*;
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
    private CarService carService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TelegramBot bot;



    public void handleMessage(Message message){
        String text = message.getText();
        Long tgId = message.getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        if (text.equals("start") || text.equals("/start")) {

            if (!profileService.getByTgId(tgId)){
                unsavedProfileService.create(tgId);
                sendMessage.setText("Telefon raqamingizni kiriting");
                bot.send(sendMessage);

            } else {
                    sendMainMenu(message.getChatId(), "Asosiy menyu");
                }

            return;

        } else if(unsavedProfileService.getByTgId(tgId) != null){

            ProfileStep step = unsavedProfileService.getProfileStepByTgId(tgId);

            switch (step){

                case NAME -> {
                    unsavedProfileService.updateName(text, tgId);
                    sendMessage.setText("Familiyangizni kiriting ");
                    bot.send(sendMessage);
                    return;
                }

                case PHONE -> {
                    unsavedProfileService.updatePhone(text, tgId);
                    sendMessage.setText("Ismingizni kiritng ");
                    bot.send(sendMessage);
                    return;
                }

                case SURNAME -> {
                    unsavedProfileService.updateSurname(text, tgId);
                    profileService.create(tgId);
                    sendMessage.setText("Ro'yhatdan o'tish muvaffaqqiyatli yakunlandi");
                    bot.send(sendMessage);
                    sendMainMenu(message.getChatId(), "Asosiy menyu");
                    return;
                }

                case CAR_DETAIL -> {
                    carService.create(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.CAR_PRICE, tgId);
                    sendMessage.setText("Avtomobilning kunlik narxini kiritng $ da ");
                    bot.send(sendMessage);
                    return;
                }

                case CAR_PRICE -> {
                    carService.updatePrice(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.DEFAULT, tgId);
                    sendMessage.setText("Avtomobilni muvaffaqqiyatli qo'shdingiz");
                    bot.send(sendMessage);
                    sendMainMenu(message.getChatId(), "Asosiy menyu");
                    return;
                }

                case UPDATE_CAR_DETAIL -> {
                    carService.updateDetail(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.DEFAULT, tgId);
                    sendMessage.setText("Avtomobil ma'lumotlari yangilandi");
                    bot.send(sendMessage);
                    sendMainMenu(message.getChatId(), "Asosiy menyu");
                    return;
                }

                case UPDATE_CAR_PRICE -> {
                    carService.updatePrice2(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.DEFAULT, tgId);
                    sendMessage.setText("Avtomobilning kunlik narxi yangilandi yangilandi");
                    bot.send(sendMessage);
                    sendMainMenu(message.getChatId(), "Asosiy menyu");
                    return;
                }

                case TAKE_CAR_STARTED_DATE -> {
                    orderService.create(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.TAKE_CAR_FINISHED_DATE, tgId);
                    sendMessage.setText("Tugash sanasini kiritng (dd-MM-yyyy formatda)");
                    bot.send(sendMessage);
                    return;
                }

                case TAKE_CAR_FINISHED_DATE -> {
                    orderService.updateFinishedDate(text, tgId);
                    unsavedProfileService.updateStep(ProfileStep.TAKE_CAR_CAR_ID, tgId);
                    List<List<InlineKeyboardButton>> rows = new LinkedList<>();
                    List<InlineKeyboardButton> row1 = new LinkedList<>();
                    row1.add(createButton("Avtomobillarni ko'rish", "/chooseCar/0"));
                    rows.add(row1);

                    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                    keyboard.setKeyboard(rows);

                    sendMessage.setText("Avtomobillarni ko'rish uchun tugmani bosing");
                    sendMessage.setReplyMarkup(keyboard);
                    bot.send(sendMessage);
                    return;
                }

            }

        }

        if (profileService.getByTgId(message.getChatId())) {
            sendMainMenu(message.getChatId(), "Asosiy menyu");
        }

    }

    public void handleCallBackQuery(CallbackQuery callbackQuery){

        String text = callbackQuery.getData();
        User user = callbackQuery.getFrom();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId().toString());

        String [] arr = text.split("/");

        switch (arr[1]) {

            case "chooseCar" -> {
                if (arr.length == 2) {
                    unsavedProfileService.updateStep(ProfileStep.TAKE_CAR_STARTED_DATE, user.getId());
                    sendMessage.setText("Boshlash sanasini kiriting (dd-MM-yyyy formatda)");
                    bot.send(sendMessage);
                    return;

                } else if (arr.length == 4) {
                    orderService.updateCarId(user.getId(), Integer.parseInt(arr[3]));
                    unsavedProfileService.updateStep(ProfileStep.DEFAULT, user.getId());
                    sendMessage.setText("Buyurtmangiz amalga oshirildi");
                    bot.send(sendMessage);
                    sendMainMenu(user.getId(), "Asosiy menyu");
                    return;
                }

                int page = Integer.parseInt(arr[2]);

                List<CarDTO> cars = carService.getAllByStatus(10, page, CarStatus.NOT_BUSY);

                StringBuilder sb = new StringBuilder();

                int index = 0;
                for (CarDTO car : cars) {
                    sb.append(++index).append(". ").append(car).append("\n");
                }

                List<List<InlineKeyboardButton>> rows = new LinkedList<>();
                List<InlineKeyboardButton> row1 = new LinkedList<>();

                for (int i = 0; i < 2; i++) {

                    List<InlineKeyboardButton> row = new LinkedList<>();

                    for (int j = 1; j <= 5; j++) {

                        InlineKeyboardButton button = createButton(String.valueOf(5 * i + j), "/chooseCar/any/" + String.valueOf(cars.get(5 * i + j - 1).getId()));
                        row.add(button);

                        if (i * 5 + j == cars.size()) {
                            row1.add(createButton("<<", "/chooseCar/" + (page == 0 ? 0 : --page)));
                            row1.add(createButton(">>", "/chooseCar/" + (++page)));
                            i = 2;
                            break;
                        }
                    }

                    rows.add(row);
                    if (!row1.isEmpty()) rows.add(row1);
                }

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                keyboard.setKeyboard(rows);

                sendMessage.setText(sb.toString());
                sendMessage.setReplyMarkup(keyboard);

                sb = new StringBuilder();
                bot.send(sendMessage);
            }

            case "addCar" -> {
                    unsavedProfileService.updateStep(ProfileStep.CAR_DETAIL, user.getId());
                    sendMessage.setText("Avtomobilning barcha ma'lumotlarni kiriting");
                    bot.send(sendMessage);
                    return;
            }

            case "orderHistory" -> {
                int page = Integer.parseInt(arr[2]);

                List<OrderEntity> orders = orderService.getAll(10, page, user.getId());
                StringBuilder sb = new StringBuilder();

                int index = 0;
                for (OrderEntity order : orders) {
                    sb.append(++index).append(". ").append(order).append("\n");
                }

                List<List<InlineKeyboardButton>> rows = new LinkedList<>();
                List<InlineKeyboardButton> row1 = new LinkedList<>();
                row1.add(createButton("<<", "/orderHistory/" + (page == 0 ? 0 : --page)));
                row1.add(createButton(">>", "/orderHistory/" + (++page)));
                rows.add(row1);

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                keyboard.setKeyboard(rows);

                sendMessage.setText(sb.toString());
                sendMessage.setReplyMarkup(keyboard);

                sb = new StringBuilder("");
                bot.send(sendMessage);
            }

            case "myCars" -> {
                int page = Integer.parseInt(arr[2]);

                List<CarDTO> cars = carService.getAll(10, page, user.getId());

                if (cars.size() == 0) {
                    sendMessage.setText("Siz avtomobil qo'shmagansiz");
                    bot.send(sendMessage);
                    sendMainMenu(user.getId(), "Asosiy menyu");
                    return;
                }

                StringBuilder sb = new StringBuilder();

                int index = 0;
                for (CarDTO car : cars) {
                    sb.append(++index).append(". ").append(car).append("\n");
                }


                List<List<InlineKeyboardButton>> rows = new LinkedList<>();
                List<InlineKeyboardButton> row1 = new LinkedList<>();


                for (int i = 0; i < 2; i++) {

                    List<InlineKeyboardButton> row = new LinkedList<>();

                    for (int j = 1; j <= 5; j++) {

                        InlineKeyboardButton button = createButton(String.valueOf(5 * i + j), "/updateCar/" + String.valueOf(cars.get(5 * i + j - 1).getId()));
                        row.add(button);

                        if (i * 5 + j == cars.size()) {
                            row1.add(createButton("<<", "/myCars/" + (page == 0 ? 0 : --page)));
                            row1.add(createButton(">>", "/myCars/" + (++page)));
                            i = 2;
                            break;
                        }
                    }

                    rows.add(row);
                    if (!row1.isEmpty()) rows.add(row1);
                }

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                keyboard.setKeyboard(rows);

                sendMessage.setText(sb.toString());
                sendMessage.setReplyMarkup(keyboard);

                sb = new StringBuilder();
                bot.send(sendMessage);
            }

            case "updateCar" -> {

                switch (arr[2]) {
                    case "detail" -> {
                        unsavedProfileService.updateStep(ProfileStep.UPDATE_CAR_DETAIL, user.getId());
                        sendMessage.setText("Avtomobilning barcha ma'lumotlarni kiriting");
                        bot.send(sendMessage);
                        break;
                    }
                    case "status" -> {
                        carService.updateStatus(user.getId());
                        sendMessage.setText("Avtomobil statusi o'zgartirildi");
                        bot.send(sendMessage);
                        sendMainMenu(user.getId(), "Asosiy menu");
                        return;
                    }
                    case "price" -> {
                        unsavedProfileService.updateStep(ProfileStep.UPDATE_CAR_PRICE, user.getId());
                        sendMessage.setText("Avtomobilning yangi kunlik narxini $ da kiriting ");
                        bot.send(sendMessage);
                        break;
                    }
                    default -> {
                        sendMessage.setText("Car menu");
                        sendMessage.setReplyMarkup(myCarsMenu());
                        // try catch
                        unsavedProfileService.updateCarId(user.getId(), Integer.parseInt(arr[2]));
                        bot.send(sendMessage);
                        break;
                    }

                }
                break;
            }

            case "showCars" -> {
                int page = Integer.parseInt(arr[2]);

                List<CarDTO> cars = carService.getAllByStatus(10, page, CarStatus.BUSY);

                StringBuilder sb = new StringBuilder();

                int index = 0;
                for (CarDTO car : cars) {
                    sb.append(++index).append(". ").append(car).append("\n");
                }

                List<List<InlineKeyboardButton>> rows = new LinkedList<>();
                List<InlineKeyboardButton> row1 = new LinkedList<>();
                row1.add(createButton("<<", "/showCars/" + (page == 0 ? 0 : --page)));
                row1.add(createButton(">>", "/showCars/" + (++page)));
                rows.add(row1);

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                keyboard.setKeyboard(rows);

                sendMessage.setText(sb.toString());
                sendMessage.setReplyMarkup(keyboard);

                sb = new StringBuilder("");
                bot.send(sendMessage);
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

        InlineKeyboardButton button4 = createButton("Mening mashinalarim","/myCars/0");
        List<InlineKeyboardButton> row4 = new LinkedList<>();
        row4.add(button4);

        InlineKeyboardButton button5 = createButton("Buyurtmalar tarixi","/orderHistory/0");
        List<InlineKeyboardButton> row5 = new LinkedList<>();
        row5.add(button5);

        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row4);
        rows.add(row5);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    public InlineKeyboardMarkup myCarsMenu(){

        InlineKeyboardButton button1 = createButton("Avtomobilning ma'lumotlarini o'zgartirish","/updateCar/detail");
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        row1.add(button1);

        InlineKeyboardButton button2 = createButton("Avtomobilning narxini o'zgartirish","/updateCar/price");
        List<InlineKeyboardButton> row2 = new LinkedList<>();
        row2.add(button2);

        InlineKeyboardButton button3 = createButton("Avtomobilning statusini o'zgartishish","/updateCar/status");
        List<InlineKeyboardButton> row3 = new LinkedList<>();
        row3.add(button3);


        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

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
