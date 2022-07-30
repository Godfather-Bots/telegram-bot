package dev.struchkov.godfather.telegram;

import dev.struchkov.godfather.telegram.context.EventDistributor;
import dev.struchkov.godfather.telegram.context.TelegramBot;
import dev.struchkov.godfather.telegram.domain.config.TelegramConnectConfig;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
public class TelegramPollingBot extends TelegramLongPollingBot implements TelegramBot {

    private final TelegramConnectConfig telegramConnectConfig;
    private EventDistributor eventDistributor;

    public TelegramPollingBot(TelegramConnectConfig telegramConnectConfig, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.telegramConnectConfig = telegramConnectConfig;
    }

    public TelegramPollingBot(TelegramConnectConfig telegramConnectConfig) {
        this.telegramConnectConfig = telegramConnectConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && eventDistributor != null) {
            eventDistributor.processing(update);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConnectConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegramConnectConfig.getBotToken();
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
