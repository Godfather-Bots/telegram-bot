package dev.struchkov.godfather.telegram.simple.context.service;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface TelegramConnect {

    AbsSender getAbsSender();

    String getToken();

    void initEventDistributor(EventDistributor eventDistributorService);

}
