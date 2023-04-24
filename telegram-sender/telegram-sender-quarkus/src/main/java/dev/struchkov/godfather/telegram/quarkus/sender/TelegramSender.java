package dev.struchkov.godfather.telegram.quarkus.sender;

import dev.struchkov.godfather.main.domain.SendType;
import dev.struchkov.godfather.quarkus.domain.BoxAnswer;
import dev.struchkov.godfather.quarkus.domain.SentBox;
import dev.struchkov.godfather.quarkus.domain.action.PreSendProcessing;
import dev.struchkov.godfather.quarkus.domain.content.send.SendAttachment;
import dev.struchkov.godfather.quarkus.domain.content.send.SendFile;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload;
import dev.struchkov.godfather.telegram.main.context.convert.MessageMailConvert;
import dev.struchkov.godfather.telegram.quarkus.context.repository.SenderRepository;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.quarkus.domain.attachment.send.PhotoSendAttachment;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_NOTIFICATION;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.ENABLE_HTML;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.ENABLE_MARKDOWN;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertKeyBoard;
import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class TelegramSender implements TelegramSending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;

    //TODO [09.12.2022|uPagge]: Доработать использование preSendProcessors
    private List<PreSendProcessing> preSendProcessors;
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
    public Uni<SentBox> send(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, true);
    }

    @Override
    public void addPreSendProcess(@NotNull PreSendProcessing processing) {
        preSendProcessors.add(processing);
    }

    @Override
    public Uni<Void> deleteMessage(@NotNull String personId, @NotNull String messageId) {
        final DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(personId);
        deleteMessage.setMessageId(Integer.parseInt(messageId));
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<SentBox> sendNotSave(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, false);
    }

    @Override
    public Uni<Void> replaceInlineMessage(String inlineMessageId, BoxAnswer boxAnswer) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final EditMessageText editMessageText = new EditMessageText();
                            editMessageText.setInlineMessageId(inlineMessageId);
                            editMessageText.setText(boxAnswer.getMessage());
                            editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));

                            boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(editMessageText::enableMarkdown);
                            boxAnswer.getPayLoad(ENABLE_HTML).ifPresent(editMessageText::enableHtml);
                            boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(
                                    isDisable -> {
                                        if (TRUE.equals(isDisable)) editMessageText.disableWebPagePreview();
                                    }
                            );

                            return Uni.createFrom().completionStage(executeAsync(editMessageText))
                                    .onFailure(TelegramApiRequestException.class).call(
                                            ex -> {
                                                final TelegramApiRequestException exception = (TelegramApiRequestException) ex;
                                                final String apiResponse = exception.getApiResponse();
                                                log.error(apiResponse, exception);
                                                return Uni.createFrom().voidItem();
                                            }
                                    ).replaceWithVoid();
                        }
                );
    }

    private Uni<SentBox> sendBoxAnswer(@NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final String recipientTelegramId = boxAnswer.getRecipientPersonId();

                            final Optional<SendInvoice> optInvoice = boxAnswer.getPayLoad(BoxAnswerPayload.INVOICE);
                            if (optInvoice.isPresent()) {
                                final SendInvoice sendInvoice = optInvoice.get();
                                return Uni.createFrom().completionStage(executeAsync(sendInvoice))
                                        .onItem().transform(ignore -> null);
                            }

                            if (boxAnswer.isReplace()) {
                                final String replaceMessageId = boxAnswer.getReplaceMessageId();
                                if (checkNotNull(replaceMessageId)) {
                                    return replace(recipientTelegramId, replaceMessageId, boxAnswer, saveMessageId);
                                } else {
                                    if (checkNotNull(senderRepository)) {
                                        return senderRepository.getLastSendMessage(recipientTelegramId)
                                                .onItem().transformToUni(
                                                        lastId -> {
                                                            if (checkNotNull(lastId)) {
                                                                return replace(recipientTelegramId, lastId, boxAnswer, saveMessageId);
                                                            } else {
                                                                return sendMessage(recipientTelegramId, boxAnswer, saveMessageId);
                                                            }
                                                        }
                                                );
                                    }
                                }
                            }

                            final SendAttachment sendAttachment = boxAnswer.getAttachment();
                            if (checkNotNull(sendAttachment)) {
                                switch (sendAttachment.getType()) {
                                    case "PHOTO":
                                        return sendPhoto(boxAnswer);
                                    case "DOCUMENT":
                                        return sendDocument(boxAnswer);
                                }
                            }

                            return sendMessage(recipientTelegramId, boxAnswer, saveMessageId);
                        }
                );
    }

    private Uni<SentBox> replace(@NotNull String telegramId, @NotNull String lastMessageId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final EditMessageText editMessageText = new EditMessageText();
                            editMessageText.setChatId(telegramId);
                            editMessageText.setMessageId(Integer.parseInt(lastMessageId));
                            editMessageText.setText(boxAnswer.getMessage());
                            editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));

                            boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(editMessageText::enableMarkdown);
                            boxAnswer.getPayLoad(ENABLE_HTML).ifPresent(editMessageText::enableHtml);
                            boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
                                if (TRUE.equals(isDisable)) editMessageText.disableWebPagePreview();
                            });

                            return Uni.createFrom().completionStage(executeAsync(editMessageText))
                                    .onItem().ifNotNull().transform(t -> {
                                        final SentBox sentBox = new SentBox();
                                        sentBox.setPersonId(telegramId);
                                        sentBox.setMessageId(lastMessageId);
                                        sentBox.setSentAnswer(boxAnswer);
                                        sentBox.setOriginalAnswer(boxAnswer);
                                        return sentBox;
                                    })
                                    .onFailure(TelegramApiRequestException.class).recoverWithUni(
                                            ex -> {
                                                final TelegramApiRequestException exception = (TelegramApiRequestException) ex;
                                                final String apiResponse = exception.getApiResponse();
                                                log.error(apiResponse, exception);
                                                if (ERROR_REPLACE_MESSAGE.equals(apiResponse)) {
                                                    return sendMessage(telegramId, boxAnswer, saveMessageId);
                                                }
                                                return Uni.createFrom().nullItem();
                                            }
                                    );
                        }
                );
    }

    private Uni<SentBox> sendMessage(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToMulti(v -> splitBoxAnswerByMessageLength(boxAnswer, 4000))
                .onItem().transformToUni(
                        sendMessage -> Uni.createFrom().completionStage(executeAsync(sendMessage))
                                .onFailure(TelegramApiRequestException.class).invoke(t -> log.error(((TelegramApiRequestException) t).getApiResponse()))
                                .onFailure().invoke(t -> log.error(t.getMessage(), t))
                ).concatenate().collect().asList()
                .call(answerMessages -> {
                    if (checkNotNull(senderRepository) && saveMessageId) {
                        return senderRepository.saveLastSendMessage(telegramId, answerMessages.get(answerMessages.size() - 1).getMessageId().toString());
                    }
                    return Uni.createFrom().nullItem();
                })
                .onItem().ifNotNull().transform(
                        answerMessages -> {
                            final Message lastMessage = answerMessages.get(answerMessages.size() - 1);

                            final SentBox sentBox = new SentBox();
                            sentBox.setPersonId(telegramId);
                            sentBox.setMessageId(lastMessage.getMessageId().toString());
                            sentBox.setOriginalAnswer(boxAnswer);
                            sentBox.setSentAnswer(boxAnswer);
                            sentBox.setSentMail(MessageMailConvert.apply(lastMessage));
                            return sentBox;
                        }
                );
    }

    private Uni<SentBox> sendPhoto(BoxAnswer boxAnswer) {
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
            if (TRUE.equals(isEnable)) sendPhoto.setParseMode(ParseMode.MARKDOWN);
        });
        boxAnswer.getPayLoad(ENABLE_HTML).ifPresent(isEnable -> {
            if (TRUE.equals(isEnable)) sendPhoto.setParseMode(ParseMode.HTML);
        });

        return Uni.createFrom().completionStage(executeAsync(sendPhoto))
                .onItem().ifNotNull().call(
                        message -> {
                            if (checkNotNull(senderRepository)) {
                                return senderRepository.saveLastSendMessage(boxAnswer.getRecipientPersonId(), message.getMessageId().toString());
                            }
                            return Uni.createFrom().voidItem();
                        }
                ).onItem().ifNotNull().transform(
                        message -> SentBox.builder()
                                .personId(boxAnswer.getRecipientPersonId())
                                .messageId(message.getMessageId().toString())
                                .sentAnswer(boxAnswer)
                                .originalAnswer(boxAnswer)
                                .sentMail(MessageMailConvert.apply(message))
                                .build()
                );
    }

    private Uni<SentBox> sendDocument(BoxAnswer boxAnswer) {
        final SendDocument sendDocument = new SendDocument();
        sendDocument.setCaption(boxAnswer.getMessage());
        sendDocument.setChatId(boxAnswer.getRecipientPersonId());
        sendDocument.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));
        sendDocument.setDocument(convertInputFile(boxAnswer.getAttachment().getSendFile()));
        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendDocument.disableNotification();
        });
        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(isEnable -> {
            if (TRUE.equals(isEnable)) sendDocument.setParseMode(ParseMode.MARKDOWN);
        });
        boxAnswer.getPayLoad(ENABLE_HTML).ifPresent(isEnable -> {
            if (TRUE.equals(isEnable)) sendDocument.setParseMode(ParseMode.HTML);
        });

        return Uni.createFrom().completionStage(executeAsync(sendDocument))
                .onItem().ifNotNull().call(
                        message -> {
                            if (checkNotNull(senderRepository)) {
                                return senderRepository.saveLastSendMessage(boxAnswer.getRecipientPersonId(), message.getMessageId().toString());
                            }
                            return Uni.createFrom().voidItem();
                        }
                ).onItem().ifNotNull().transform(
                        message -> SentBox.builder()
                                .personId(boxAnswer.getRecipientPersonId())
                                .messageId(message.getMessageId().toString())
                                .sentAnswer(boxAnswer)
                                .originalAnswer(boxAnswer)
                                .sentMail(MessageMailConvert.apply(message))
                                .build()
                );
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

    public Multi<SendMessage> splitBoxAnswerByMessageLength(BoxAnswer boxAnswer, int maxMessageLength) {
        final List<SendMessage> split = new ArrayList<>();
        String message = boxAnswer.getMessage();

        while (message.length() > maxMessageLength) {
            String subMessage = message.substring(0, maxMessageLength);
            message = message.substring(maxMessageLength);
            split.add(createNewTextAnswer(boxAnswer, subMessage));
        }

        split.add(createNewTextAnswer(boxAnswer, message));

        return Multi.createFrom().iterable(split);
    }

    private SendMessage createNewTextAnswer(BoxAnswer boxAnswer, String subMessage) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(boxAnswer.getRecipientPersonId());
        sendMessage.setText(subMessage);
        sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));

        boxAnswer.getPayLoad(ENABLE_MARKDOWN).ifPresent(sendMessage::enableMarkdown);
        boxAnswer.getPayLoad(ENABLE_HTML).ifPresent(sendMessage::enableHtml);
        boxAnswer.getPayLoad(DISABLE_WEB_PAGE_PREVIEW).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableWebPagePreview();
        });
        boxAnswer.getPayLoad(DISABLE_NOTIFICATION).ifPresent(isDisable -> {
            if (TRUE.equals(isDisable)) sendMessage.disableNotification();
        });
        return sendMessage;
    }

    private CompletableFuture<Message> executeAsync(SendMessage sendMessage) {
        try {
            return absSender.executeAsync(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return completedFuture(null);
    }

    private CompletableFuture<Serializable> executeAsync(EditMessageText editMessageText) {
        try {
            return absSender.executeAsync(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return completedFuture(null);
    }

    private CompletableFuture<Message> executeAsync(SendInvoice sendInvoice) {
        try {
            return absSender.executeAsync(sendInvoice);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return completedFuture(null);
    }

    private CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) {
        return absSender.executeAsync(sendPhoto);
    }

    private CompletableFuture<Message> executeAsync(SendDocument sendDocument) {
        return absSender.executeAsync(sendDocument);
    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
