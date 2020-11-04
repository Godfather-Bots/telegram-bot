package org.sadtech.telegram.bot.listen;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventDistributor {

    void processing(@NonNull Update update);

}

