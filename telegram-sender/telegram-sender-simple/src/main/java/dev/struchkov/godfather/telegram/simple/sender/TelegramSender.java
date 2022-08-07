package dev.struchkov.godfather.telegram.simple.sender;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.SendType;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert;
import dev.struchkov.godfather.telegram.simple.context.service.SenderStorageService;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Optional;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class TelegramSender implements TelegramSending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;

    private SendPreProcessing sendPreProcessing;
    private SenderStorageService senderStorageService;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    public TelegramSender(TelegramConnect telegramConnect, SenderStorageService senderStorageService) {
        this.absSender = telegramConnect.getAbsSender();
        this.senderStorageService = senderStorageService;
    }

    public void setSendPreProcessing(SendPreProcessing sendPreProcessing) {
        this.sendPreProcessing = sendPreProcessing;
    }

    public void setSenderRepository(SenderStorageService senderStorageService) {
        this.senderStorageService = senderStorageService;
    }

    @Override
    public void send(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer) {
        sendBoxAnswer(telegramId, boxAnswer, true);
    }

    @Override
    public void sendNotSave(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer) {
        sendBoxAnswer(telegramId, boxAnswer, false);
    }

    private void sendBoxAnswer(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        isNotNull(telegramId, boxAnswer);
        try {
            if (boxAnswer.isReplace() && checkNotNull(senderStorageService)) {
                final Optional<Integer> optLastId = senderStorageService.getLastSendMessage(telegramId);
                if (optLastId.isPresent()) {
                    replaceMessage(telegramId, optLastId.get(), boxAnswer);
                } else {
                    sendMessage(telegramId, boxAnswer, saveMessageId);
                }
            } else {
                sendMessage(telegramId, boxAnswer, saveMessageId);
            }
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
            if (ERROR_REPLACE_MESSAGE.equals(e.getApiResponse())) {
                sendMessage(telegramId, boxAnswer, saveMessageId);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void replaceMessage(@NotNull Long telegramId, @NotNull Integer lastMessageId, @NotNull BoxAnswer boxAnswer) throws TelegramApiException {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(telegramId));
        editMessageText.setMessageId(lastMessageId);
        editMessageText.enableMarkdown(true);
        editMessageText.setText(boxAnswer.getMessage());
        editMessageText.setReplyMarkup(KeyBoardConvert.convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
        absSender.execute(editMessageText);
    }

    private void sendMessage(@NotNull Long telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(telegramId));
        sendMessage.setText(
                sendPreProcessing != null
                        ? sendPreProcessing.pretreatment(boxAnswer.getMessage())
                        : boxAnswer.getMessage()
        );
        sendMessage.setReplyMarkup(KeyBoardConvert.convertKeyBoard(boxAnswer.getKeyBoard()));
        try {
            final Message execute = absSender.execute(sendMessage);
            if (checkNotNull(senderStorageService) && saveMessageId) {
                senderStorageService.saveLastSendMessage(telegramId, execute.getMessageId());
            }
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
