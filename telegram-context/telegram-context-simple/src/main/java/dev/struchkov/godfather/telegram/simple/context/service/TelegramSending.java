package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.simple.context.service.SendingService;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.SentBox;

import java.util.Optional;

public interface TelegramSending extends SendingService {

    Optional<SentBox> sendNotSave(BoxAnswer boxAnswer);

    void replaceInlineMessage(String inlineMessageId, BoxAnswer boxAnswer);

}
