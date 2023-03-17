package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class TelegramWebhookBot extends org.telegram.telegrambots.bots.TelegramWebhookBot implements TelegramBot {

    private final TelegramBotConfig telegramBotConfig;
    private EventDistributor eventDistributor;

    public TelegramWebhookBot(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        eventDistributor.processing(update);
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public AbsSender getAdsSender() {
        return this;
    }

    @Override
    public void initEventDistributor(EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
