package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.simple.context.service.Sending;
import org.jetbrains.annotations.NotNull;

public interface TelegramSending extends Sending {

    void sendNotSave(@NotNull BoxAnswer boxAnswer);

}
