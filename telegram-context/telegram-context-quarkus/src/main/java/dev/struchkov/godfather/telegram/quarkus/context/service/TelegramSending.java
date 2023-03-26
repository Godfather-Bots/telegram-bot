package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.quarkus.context.service.SendingService;
import dev.struchkov.godfather.quarkus.domain.BoxAnswer;
import dev.struchkov.godfather.quarkus.domain.SentBox;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

public interface TelegramSending extends SendingService {

    Uni<SentBox> sendNotSave(@NotNull BoxAnswer boxAnswer);

    Uni<Void> replaceInlineMessage(String inlineMessageId, BoxAnswer boxAnswer);

}
