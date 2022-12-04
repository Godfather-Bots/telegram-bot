package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.quarkus.context.service.Sending;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

public interface TelegramSending extends Sending {

    Uni<Void> sendNotSave(@NotNull String personId, @NotNull BoxAnswer boxAnswer);

}
