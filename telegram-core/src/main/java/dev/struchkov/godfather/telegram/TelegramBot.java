package dev.struchkov.godfather.telegram;

import dev.struchkov.godfather.telegram.listen.EventDistributorImpl;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * TODO: Добавить описание интерфейса.
 *
 * @author upagge [12.02.2020]
 */
public interface TelegramBot {

    AbsSender getAdsSender();

    void initEventDistributor(@NotNull EventDistributorImpl eventDistributor);

}
