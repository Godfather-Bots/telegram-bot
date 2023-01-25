package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.SentBox;
import dev.struchkov.godfather.simple.context.service.SendingService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface TelegramSending extends SendingService {

    Optional<SentBox> sendNotSave(@NotNull BoxAnswer boxAnswer);

}
