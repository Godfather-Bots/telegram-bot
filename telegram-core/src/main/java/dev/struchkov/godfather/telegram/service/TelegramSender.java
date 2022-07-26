package dev.struchkov.godfather.telegram.service;

import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.service.sender.SendType;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.telegram.TelegramConnect;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.HashMap;
import java.util.Map;

import static dev.struchkov.godfather.telegram.utils.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.godfather.telegram.utils.KeyBoardConvert.convertKeyBoard;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class TelegramSender implements Sending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;
    private final Map<Long, Integer> lastMessageId = new HashMap<>();

    private SendPreProcessing sendPreProcessing;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAdsSender();
    }

    public void setSendPreProcessing(SendPreProcessing sendPreProcessing) {
        this.sendPreProcessing = sendPreProcessing;
    }

    public void send(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer) {
        isNotNull(telegramId, boxAnswer);
        try {
            if (boxAnswer.isReplace() && lastMessageId.containsKey(telegramId)) {
                replaceMessage(telegramId, boxAnswer);
            } else {
                sendMessage(telegramId, boxAnswer);
            }
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
            if (ERROR_REPLACE_MESSAGE.equals(e.getApiResponse())) {
                sendMessage(telegramId, boxAnswer);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void replaceMessage(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer) throws TelegramApiException {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(telegramId));
        editMessageText.setMessageId(lastMessageId.get(telegramId));
        editMessageText.enableMarkdown(true);
        editMessageText.setText(boxAnswer.getMessage());
        editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
        absSender.execute(editMessageText);
    }

    private void sendMessage(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(telegramId));
        sendMessage.setText(
                sendPreProcessing != null
                        ? sendPreProcessing.pretreatment(boxAnswer.getMessage())
                        : boxAnswer.getMessage()
        );
        sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));
        try {
            final Message execute = absSender.execute(sendMessage);
            lastMessageId.put(telegramId, execute.getMessageId());
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    public SendType getType() {
        return SendType.PRIVATE;
    }

}
