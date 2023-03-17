package dev.struchkov.godfather.telegram.simple.sender;

import dev.struchkov.godfather.main.domain.SendType;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.SentBox;
import dev.struchkov.godfather.simple.domain.action.PreSendProcessing;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload;
import dev.struchkov.godfather.telegram.simple.context.repository.SenderRepository;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_NOTIFICATION;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertKeyBoard;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;

public class TelegramSender implements TelegramSending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;

    private final List<PreSendProcessing> preSendProcessors = new ArrayList<>();
    private SenderRepository senderRepository;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    public TelegramSender(TelegramConnect telegramConnect, SenderRepository senderRepository) {
        this.absSender = telegramConnect.getAbsSender();
        this.senderRepository = senderRepository;
    }

    public void setSenderRepository(SenderRepository senderRepository) {
        this.senderRepository = senderRepository;
    }

    @Override
    public void addPreSendProcess(@NotNull PreSendProcessing processing) {
        preSendProcessors.add(processing);
    }

    @Override
    public void deleteMessage(@NotNull String personId, @NotNull String messageId) {
        final DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(personId);
        deleteMessage.setMessageId(parseInt(messageId));
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Optional<SentBox> replaceMessage(@NotNull String personId, @NotNull String messageId, @NotNull BoxAnswer newAnswer) {
        return replace(personId, messageId, newAnswer, newAnswer, true);
    }

    @Override
    public Optional<SentBox> send(@NotNull BoxAnswer boxAnswer) {
        isNotNull(boxAnswer.getRecipientPersonId());
        return sendBoxAnswer(boxAnswer, true);
    }

    @Override
    public Optional<SentBox> sendNotSave(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, false);
    }

    private Optional<SentBox> sendBoxAnswer(BoxAnswer boxAnswer, boolean saveMessageId) {
        final String recipientTelegramId = boxAnswer.getRecipientPersonId();
        isNotNull(recipientTelegramId);

        final Optional<SendInvoice> optInvoice = boxAnswer.getPayLoad(BoxAnswerPayload.INVOICE);
        if (optInvoice.isPresent()) {
            final SendInvoice sendInvoice = optInvoice.get();
            try {
                sendInvoice.validate();
                absSender.execute(sendInvoice);
                return Optional.empty();
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
                return Optional.empty();
            }
        }

        BoxAnswer preparedAnswer = boxAnswer;
        for (PreSendProcessing preSendProcessor : preSendProcessors) {
            preparedAnswer = preSendProcessor.pretreatment(boxAnswer);
        }

        if (preparedAnswer.isReplace()) {
            final String replaceMessageId = preparedAnswer.getReplaceMessageId();
            if (checkNotNull(replaceMessageId)) {
                return replace(recipientTelegramId, replaceMessageId, boxAnswer, preparedAnswer, saveMessageId);
            } else {
                if (checkNotNull(senderRepository)) {
                    final Optional<String> optLastId = senderRepository.getLastSendMessage(recipientTelegramId);
                    if (optLastId.isPresent()) {
                        return replace(recipientTelegramId, optLastId.get(), boxAnswer, preparedAnswer, saveMessageId);
                    }
                }
            }
        }
        return sendMessage(recipientTelegramId, boxAnswer, preparedAnswer, saveMessageId);
    }

    private Optional<SentBox> replace(@NotNull String telegramId, @NotNull String replaceMessageId, @NotNull BoxAnswer boxAnswer, BoxAnswer preparedAnswer, boolean saveMessageId) {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(telegramId);
        editMessageText.setMessageId(parseInt(replaceMessageId));
        editMessageText.enableMarkdown(true);
        editMessageText.setText(boxAnswer.getMessage());
        editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
        try {
            absSender.execute(editMessageText);
            return SentBox.optional(telegramId, replaceMessageId, preparedAnswer, boxAnswer);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
            if (ERROR_REPLACE_MESSAGE.equals(e.getApiResponse())) {
                return sendMessage(telegramId, preparedAnswer, preparedAnswer, saveMessageId);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Optional<SentBox> sendMessage(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer, BoxAnswer preparedAnswer, boolean saveMessageId) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(telegramId);
        sendMessage.setText(boxAnswer.getMessage());
        sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableWebPagePreview();
        });
        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableNotification();
        });

        try {
            final Message execute = absSender.execute(sendMessage);
            if (checkNotNull(senderRepository) && saveMessageId) {
                senderRepository.saveLastSendMessage(telegramId, execute.getMessageId().toString());
            }
            return SentBox.optional(telegramId, execute.getMessageId().toString(), preparedAnswer, boxAnswer);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
