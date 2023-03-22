package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.domain.ClientBotCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface TelegramService {

    void executeAction(@NotNull String personId, ChatAction chatAction);

    void pinMessage(@NotNull String personId, @NotNull String messageId);

    void unPinMessage(@NotNull String personId, @NotNull String messageId);

    void addCommand(@NotNull Collection<ClientBotCommand> botCommands);

    boolean checkChatMember(@NotNull String personId, @NotNull String chatIdOrChannelId);

}
