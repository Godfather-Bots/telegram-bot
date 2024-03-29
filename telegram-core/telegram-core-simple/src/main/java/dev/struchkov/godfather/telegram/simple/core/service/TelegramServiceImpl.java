package dev.struchkov.godfather.telegram.simple.core.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TelegramServiceImpl implements TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramServiceImpl.class);

    private final AbsSender absSender;

    public TelegramServiceImpl(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    @Override
    public void executeAction(@NotNull String personId, ChatAction chatAction) {
        final SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(personId);
        sendChatAction.setAction(ActionType.valueOf(chatAction.name()));
        try {
            absSender.execute(sendChatAction);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void pinMessage(@NotNull String personId, @NotNull String messageId) {
        final PinChatMessage pinChatMessage = new PinChatMessage();
        pinChatMessage.setChatId(personId);
        pinChatMessage.setMessageId(Integer.parseInt(messageId));
        try {
            absSender.execute(pinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void unPinMessage(@NotNull String personId, @NotNull String messageId) {
        final UnpinChatMessage unpinChatMessage = new UnpinChatMessage();
        unpinChatMessage.setChatId(personId);
        unpinChatMessage.setMessageId(Integer.parseInt(messageId));
        try {
            absSender.execute(unpinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
