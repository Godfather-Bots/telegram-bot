package dev.struchkov.godfather.telegram.context;

import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.service.sender.Sending;
import org.jetbrains.annotations.NotNull;

public interface TelegramSending extends Sending {

    void sendNotSave(@NotNull Long personId, @NotNull BoxAnswer boxAnswer);

}
