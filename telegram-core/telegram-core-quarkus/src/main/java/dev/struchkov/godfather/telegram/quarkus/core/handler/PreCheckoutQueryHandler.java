package dev.struchkov.godfather.telegram.quarkus.core.handler;

import dev.struchkov.godfather.quarkus.context.service.EventHandler;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerShippingQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor
public class PreCheckoutQueryHandler implements EventHandler<PreCheckoutQuery> {

    private final TelegramConnect telegramConnect;

    @Override
    public Uni<Void> handle(PreCheckoutQuery event) {
        final AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
        answerPreCheckoutQuery.setPreCheckoutQueryId(event.getId());
        answerPreCheckoutQuery.setOk(true);
        try {
            answerPreCheckoutQuery.validate();
        } catch (TelegramApiValidationException e) {
            log.error(e.getMessage(), e);
        }
        return Uni.createFrom().completionStage(
                execAsync(answerPreCheckoutQuery)
        ).replaceWithVoid();
    }

    public CompletionStage<Object> execAsync(AnswerPreCheckoutQuery answerShippingQuery) {
        try {
            telegramConnect.getAbsSender().executeAsync(answerShippingQuery);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return CompletableFuture.completedStage(null);
    }

    @Override
    public String getEventType() {
        return PreCheckoutQuery.class.getName();
    }

}
