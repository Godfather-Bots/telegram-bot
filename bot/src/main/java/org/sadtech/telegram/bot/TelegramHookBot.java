//package org.sadtech.telegram.bot;
//
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.sadtech.telegram.bot.config.TelegramWebHookConfig;
//import org.sadtech.telegram.bot.listen.EventDistributor;
//import org.sadtech.telegram.bot.listen.EventDistributorImpl;
//import org.telegram.telegrambots.bots.TelegramWebhookBot;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//
///**
// * TODO: Добавить описание класса.
// *
// * @author upagge [12.02.2020]
// */
//@RequiredArgsConstructor
//public class TelegramHookBot extends TelegramWebhookBot implements TelegramBot {
//
//    private final TelegramWebHookConfig webHookConfig;
//    private EventDistributor eventDistributor;
//
//    @Override
//    public BotApiMethod onWebhookUpdateReceived(Update update) {
//        return eventDistributor.processing(update);
//    }
//
//    @Override
//    public String getBotUsername() {
//        return webHookConfig.getBotUsername();
//    }
//
//    @Override
//    public String getBotToken() {
//        return webHookConfig.getBotToken();
//    }
//
//    @Override
//    public String getBotPath() {
//        return null;
//    }
//
//    @Override
//    public AbsSender getAdsSender() {
//        return this;
//    }
//
//    @Override
//    public void initEventDistributor(@NonNull EventDistributorImpl eventDistributor) {
//        this.eventDistributor = eventDistributor;
//    }
//
//}
