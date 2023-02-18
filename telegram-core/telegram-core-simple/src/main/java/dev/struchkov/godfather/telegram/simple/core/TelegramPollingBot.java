package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramBot;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
public class TelegramPollingBot extends TelegramLongPollingBot implements TelegramBot {

    private final TelegramBotConfig telegramBotConfig;
    private EventDistributor eventDistributor;

    public TelegramPollingBot(TelegramBotConfig telegramBotConfig, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.telegramBotConfig = telegramBotConfig;
    }

    public TelegramPollingBot(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (checkNotNull(update) && checkNotNull(eventDistributor)) {
            eventDistributor.processing(update);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public AbsSender getAdsSender() {
        return this;
    }

    @Override
    public void initEventDistributor(@NotNull EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
