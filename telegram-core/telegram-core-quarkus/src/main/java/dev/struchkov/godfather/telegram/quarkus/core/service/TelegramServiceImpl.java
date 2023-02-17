package dev.struchkov.godfather.telegram.quarkus.core.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramService;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.concurrent.CompletableFuture;

public class TelegramServiceImpl implements TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramServiceImpl.class);

    private final AbsSender absSender;

    public TelegramServiceImpl(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    @Override
    public Uni<Void> executeAction(@NotNull String personId, ChatAction chatAction) {
        final SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(personId);
        sendChatAction.setAction(ActionType.valueOf(chatAction.name()));

        return Uni.createFrom().completionStage(getExecuteAsync(sendChatAction))
                .replaceWithVoid();
    }

    private CompletableFuture<Boolean> getExecuteAsync(SendChatAction sendChatAction) {
        try {
            return absSender.executeAsync(sendChatAction);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

}
