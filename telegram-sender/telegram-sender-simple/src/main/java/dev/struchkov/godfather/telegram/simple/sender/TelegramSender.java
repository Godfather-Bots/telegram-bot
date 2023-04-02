package dev.struchkov.godfather.telegram.simple.sender;

import dev.struchkov.godfather.main.domain.SendType;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.SentBox;
import dev.struchkov.godfather.simple.domain.action.PreSendProcessing;
import dev.struchkov.godfather.simple.domain.content.send.SendAttachment;
import dev.struchkov.godfather.simple.domain.content.send.SendFile;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload;
import dev.struchkov.godfather.telegram.main.context.convert.MessageMailConvert;
import dev.struchkov.godfather.telegram.simple.context.repository.SenderRepository;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.simple.domain.attachment.send.PhotoSendAttachment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_NOTIFICATION;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.ENABLE_MARKDOWN;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertKeyBoard;
import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
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
    public Optional<SentBox> send(@NotNull BoxAnswer boxAnswer) {
        isNotNull(boxAnswer.getRecipientPersonId());
        return sendBoxAnswer(boxAnswer, true);
    }

    @Override
    public Optional<SentBox> sendNotSave(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, false);
    }

    @Override
    public void replaceInlineMessage(String inlineMessageId, BoxAnswer boxAnswer) {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setInlineMessageId(inlineMessageId);
        editMessageText.setText(boxAnswer.getMessage());
        editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(editMessageText::enableMarkdown);
        boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) editMessageText.disableWebPagePreview();
        });

        try {
            absSender.execute(editMessageText);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
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

        final SendAttachment sendAttachment = boxAnswer.getAttachment();
        if (checkNotNull(sendAttachment)) {
            switch (sendAttachment.getType()) {
                case "PHOTO":
                    return sendPhoto(boxAnswer, preparedAnswer);
                case "DOCUMENT":
                    return sendDocument(boxAnswer, preparedAnswer);
            }
        }
        return sendMessage(recipientTelegramId, boxAnswer, preparedAnswer, saveMessageId);
    }

    private Optional<SentBox> replace(@NotNull String telegramId, @NotNull String replaceMessageId, @NotNull BoxAnswer boxAnswer, BoxAnswer preparedAnswer, boolean saveMessageId) {
        final EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(telegramId);
        editMessageText.setMessageId(parseInt(replaceMessageId));
        editMessageText.setText(boxAnswer.getMessage());
        editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(editMessageText::enableMarkdown);
        boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) editMessageText.disableWebPagePreview();
        });

        try {
            absSender.execute(editMessageText);
            return Optional.of(
                    SentBox.builder()
                            .personId(telegramId)
                            .messageId(replaceMessageId)
                            .originalAnswer(boxAnswer)
                            .sentAnswer(boxAnswer)
                            .build()
            );
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
        final List<SendMessage> sendMessages = splitBoxAnswerByMessageLength(boxAnswer, 4000);

        Message execute = null;
        for (SendMessage sendMessage : sendMessages) {
            try {
                execute = absSender.execute(sendMessage);
            } catch (TelegramApiRequestException e) {
                log.error(e.getApiResponse());
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
        if (checkNotNull(execute)) {
            if (checkNotNull(senderRepository) && saveMessageId) {
                senderRepository.saveLastSendMessage(telegramId, execute.getMessageId().toString());
            }
            return Optional.of(
                    SentBox.builder()
                            .personId(telegramId)
                            .messageId(execute.getMessageId().toString())
                            .sentAnswer(boxAnswer)
                            .originalAnswer(boxAnswer)
                            .sentMail(MessageMailConvert.apply(execute))
                            .build()
            );
        }
        return Optional.empty();
    }

    private Optional<SentBox> sendPhoto(BoxAnswer boxAnswer, BoxAnswer preparedAnswer) {
        final PhotoSendAttachment photoSendAttachment = (PhotoSendAttachment) boxAnswer.getAttachment();
        final SendFile sendFile = photoSendAttachment.getSendFile();

        final SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(boxAnswer.getMessage());
        sendPhoto.setChatId(boxAnswer.getRecipientPersonId());
        sendPhoto.setPhoto(convertInputFile(sendFile));
        sendPhoto.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendPhoto.disableNotification();
        });
        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(isEnable -> {
            if (TRUE.equals(isEnable)) sendPhoto.setParseMode("Markdown");
        });

        Message execute = null;
        try {
            execute = absSender.execute(sendPhoto);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        if (checkNotNull(execute)) {
            if (checkNotNull(senderRepository)) {
                senderRepository.saveLastSendMessage(boxAnswer.getRecipientPersonId(), execute.getMessageId().toString());
            }
            return Optional.of(
                    SentBox.builder()
                            .personId(boxAnswer.getRecipientPersonId())
                            .messageId(execute.getMessageId().toString())
                            .sentAnswer(boxAnswer)
                            .originalAnswer(boxAnswer)
                            .sentMail(MessageMailConvert.apply(execute))
                            .build()
            );
        }
        return Optional.empty();
    }

    private Optional<SentBox> sendDocument(BoxAnswer boxAnswer, BoxAnswer preparedAnswer) {
        final SendDocument sendDocument = new SendDocument();
        sendDocument.setCaption(boxAnswer.getMessage());
        sendDocument.setChatId(boxAnswer.getRecipientPersonId());
        sendDocument.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));
        sendDocument.setDocument(convertInputFile(boxAnswer.getAttachment().getSendFile()));
        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendDocument.disableNotification();
        });
        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(isEnable -> {
            if (TRUE.equals(isEnable)) sendDocument.setParseMode("Markdown");
        });

        Message execute = null;
        try {
            execute = absSender.execute(sendDocument);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        if (checkNotNull(execute)) {
            if (checkNotNull(senderRepository)) {
                senderRepository.saveLastSendMessage(boxAnswer.getRecipientPersonId(), execute.getMessageId().toString());
            }
            return Optional.of(
                    SentBox.builder()
                            .personId(boxAnswer.getRecipientPersonId())
                            .messageId(execute.getMessageId().toString())
                            .sentAnswer(boxAnswer)
                            .originalAnswer(boxAnswer)
                            .sentMail(MessageMailConvert.apply(execute))
                            .build()
            );
        }
        return Optional.empty();
    }

    private InputFile convertInputFile(SendFile sendFile) {
        final File fileData = sendFile.getData();
        final String fileName = sendFile.getFileName();

        if (checkNotBlank(sendFile.getFileId())) {
            return new InputFile(sendFile.getFileId());
        }

        if (checkNotBlank(sendFile.getUrl())) {
            return new InputFile(sendFile.getUrl());
        }

        if (checkNotNull(fileData)) {
            if (checkNotBlank(fileName)) {
                return new InputFile(fileData, fileName);
            } else {
                return new InputFile(fileData);
            }
        }

        if (checkNotNull(sendFile.getFileStream())) {
            return new InputFile(sendFile.getFileStream(), fileName);
        } else {
            return new InputFile(fileName);
        }

    }

    public List<SendMessage> splitBoxAnswerByMessageLength(BoxAnswer boxAnswer, int maxMessageLength) {
        final List<SendMessage> split = new ArrayList<>();
        String message = boxAnswer.getMessage();

        while (message.length() > maxMessageLength) {
            String subMessage = message.substring(0, maxMessageLength);
            message = message.substring(maxMessageLength);
            split.add(createNewMessage(boxAnswer, subMessage));
        }

        split.add(createNewMessage(boxAnswer, message));

        return split;
    }

    private SendMessage createNewMessage(BoxAnswer boxAnswer, String subMessage) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(boxAnswer.getRecipientPersonId());
        sendMessage.setText(subMessage);
        sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(sendMessage::enableMarkdown);
        boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableWebPagePreview();
        });
        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableNotification();
        });
        return sendMessage;
    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
