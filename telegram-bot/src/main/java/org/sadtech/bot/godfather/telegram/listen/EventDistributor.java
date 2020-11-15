package org.sadtech.bot.godfather.telegram.listen;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventDistributor {

    void processing(@NonNull Update update);

}

