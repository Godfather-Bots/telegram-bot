package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

public interface TelegramService {

    Uni<Void> executeAction(@NotNull String personId, ChatAction chatAction);

}
