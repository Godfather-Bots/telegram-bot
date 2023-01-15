package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.SentBox;
import dev.struchkov.godfather.quarkus.context.service.SendingService;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

public interface TelegramSending extends SendingService<Integer> {

    Uni<SentBox<Integer>> sendNotSave(@NotNull BoxAnswer boxAnswer);

}
