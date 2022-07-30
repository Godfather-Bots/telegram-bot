package dev.struchkov.godfather.telegram.context;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;

public interface EventDistributor {

    void processing(@NotNull Update update);

}

