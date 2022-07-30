package dev.struchkov.godfather.telegram.context;

import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.validation.constraints.NotNull;

/**
 * TODO: Добавить описание интерфейса.
 *
 * @author upagge [12.02.2020]
 */
public interface TelegramBot {

    AbsSender getAdsSender();

    void initEventDistributor(@NotNull EventDistributor eventDistributor);

}
