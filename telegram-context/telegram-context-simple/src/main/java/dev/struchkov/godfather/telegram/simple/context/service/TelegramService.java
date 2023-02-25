package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import org.jetbrains.annotations.NotNull;

public interface TelegramService {

    void executeAction(@NotNull String personId, ChatAction chatAction);

    void pinMessage(@NotNull String personId, @NotNull String messageId);

    void unPinMessage(@NotNull String personId, @NotNull String messageId);

}
