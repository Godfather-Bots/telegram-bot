package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.domain.ClientBotCommand;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface TelegramService {

    Uni<Void> executeAction(@NotNull String personId, ChatAction chatAction);

    Uni<Void> pinMessage(@NotNull String personId, @NotNull String messageId);

    Uni<Void> unPinMessage(@NotNull String personId, @NotNull String messageId);

    Uni<Void> addCommand(@NotNull Collection<ClientBotCommand> botCommands);

    Uni<Boolean> checkChatMember(@NotNull String personId, @NotNull String chatIdOrChannelId);

}
