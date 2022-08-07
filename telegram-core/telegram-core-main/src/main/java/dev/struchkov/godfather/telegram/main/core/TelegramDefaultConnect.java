package dev.struchkov.godfather.telegram.main.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramConnectConfig;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class TelegramDefaultConnect implements TelegramConnect {

    private final String botToken;
    private final AbsSender absSender;

    public TelegramDefaultConnect(TelegramConnectConfig telegramConnectConfig) {
        this.botToken = telegramConnectConfig.getBotToken();
        this.absSender = new DefaultAbsSender(new DefaultBotOptions()) {
            @Override
            public String getBotToken() {
                return botToken;
            }
        };
    }

    @Override
    public AbsSender getAbsSender() {
        return absSender;
    }

    @Override
    public String getToken() {
        return botToken;
    }

}
