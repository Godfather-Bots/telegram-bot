package dev.struchkov.godfather.telegram.context;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface TelegramConnect {

    AbsSender getAbsSender();

    String getToken();

}
