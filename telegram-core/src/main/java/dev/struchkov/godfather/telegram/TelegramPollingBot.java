package dev.struchkov.godfather.telegram;

import dev.struchkov.godfather.telegram.config.TelegramPollingConfig;
import dev.struchkov.godfather.telegram.listen.EventDistributor;
import dev.struchkov.godfather.telegram.listen.EventDistributorService;
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
        if (update != null && eventDistributor != null) {
            eventDistributor.processing(update);
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
    public void initEventDistributor(@NotNull EventDistributorService eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
