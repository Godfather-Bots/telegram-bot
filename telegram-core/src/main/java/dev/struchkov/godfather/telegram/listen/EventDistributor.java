package dev.struchkov.godfather.telegram.listen;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventDistributor {

    void processing(@NotNull Update update);

}

