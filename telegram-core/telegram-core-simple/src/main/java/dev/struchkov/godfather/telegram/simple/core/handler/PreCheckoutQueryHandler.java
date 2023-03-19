package dev.struchkov.godfather.telegram.simple.core.handler;

import dev.struchkov.godfather.simple.context.service.EventHandler;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
public class PreCheckoutQueryHandler implements EventHandler<PreCheckoutQuery> {

    private final TelegramConnect telegramConnect;

    @Override
    public void handle(PreCheckoutQuery event) {
        final AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
        answerPreCheckoutQuery.setPreCheckoutQueryId(event.getId());
        answerPreCheckoutQuery.setOk(true);
        try {
            answerPreCheckoutQuery.validate();
            telegramConnect.getAbsSender().execute(answerPreCheckoutQuery);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public String getEventType() {
        return PreCheckoutQuery.class.getSimpleName();
    }

}