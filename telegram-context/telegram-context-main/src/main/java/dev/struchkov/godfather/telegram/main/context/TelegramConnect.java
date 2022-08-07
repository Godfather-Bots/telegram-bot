package dev.struchkov.godfather.telegram.main.context;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface TelegramConnect {

    AbsSender getAbsSender();

    String getToken();

}
