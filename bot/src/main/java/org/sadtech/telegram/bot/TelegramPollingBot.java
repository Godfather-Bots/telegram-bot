package org.sadtech.telegram.bot;

import lombok.NonNull;
import org.sadtech.telegram.bot.config.TelegramPollingConfig;
import org.sadtech.telegram.bot.listen.EventDistributor;
import org.sadtech.telegram.bot.listen.EventDistributorImpl;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
public class TelegramPollingBot extends TelegramLongPollingBot implements TelegramBot {

    private final TelegramPollingConfig telegramPollingConfig;
    private EventDistributor eventDistributor;

    public TelegramPollingBot(TelegramPollingConfig telegramPollingConfig, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.telegramPollingConfig = telegramPollingConfig;
    }

    public TelegramPollingBot(TelegramPollingConfig telegramPollingConfig) {
        this.telegramPollingConfig = telegramPollingConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (eventDistributor != null) {
            Optional.ofNullable(update).ifPresent(newUpdate -> eventDistributor.processing(update));
        }
    }

    @Override
    public String getBotUsername() {
        return telegramPollingConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegramPollingConfig.getBotToken();
    }

    @Override
    public AbsSender getAdsSender() {
        return this;
    }

    @Override
    public void initEventDistributor(@NonNull EventDistributorImpl eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
