package dev.struchkov.godfather.telegram.quarkus.sender;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.SendType;
import dev.struchkov.godfather.main.domain.SentBox;
import dev.struchkov.godfather.quarkus.context.service.PreSendProcessing;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert;
import dev.struchkov.godfather.telegram.quarkus.context.repository.SenderRepository;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramSending;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;

import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

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
    public Uni<SentBox<Integer>> send(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, true);
    }

    @Override
    public void addPreSendProcess(@NotNull PreSendProcessing processing) {
        preSendProcessors.add(processing);
    }

    @Override
    public Uni<Void> deleteMessage(@NotNull String personId, @NotNull Integer messageId) {
        final DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(personId);
        deleteMessage.setMessageId(messageId);
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<SentBox<Integer>> replaceMessage(@NotNull String personId, @NotNull Integer messageId, @NotNull BoxAnswer newAnswer) {
        return replace(personId, messageId, newAnswer, true);
    }

    @Override
    public Uni<SentBox<Integer>> sendNotSave(@NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(boxAnswer, false);
    }

    private Uni<SentBox<Integer>> sendBoxAnswer(@NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final String recipientTelegramId = boxAnswer.getRecipientPersonId();
                            if (boxAnswer.isReplace() && checkNotNull(senderRepository)) {
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
                            } else {
                                return sendMessage(recipientTelegramId, boxAnswer, saveMessageId);
                            }
                        }
                );
    }

    private Uni<SentBox<Integer>> replace(@NotNull String telegramId, @NotNull Integer lastMessageId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final EditMessageText editMessageText = new EditMessageText();
                            editMessageText.setChatId(telegramId);
                            editMessageText.setMessageId(lastMessageId);
                            editMessageText.enableMarkdown(true);
                            editMessageText.setText(boxAnswer.getMessage());
                            editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
                            try {
                                absSender.execute(editMessageText);
                                return Uni.createFrom().optional(SentBox.optional(lastMessageId, boxAnswer, boxAnswer));
                            } catch (TelegramApiRequestException e) {
                                log.error(e.getApiResponse());
                                if (ERROR_REPLACE_MESSAGE.equals(e.getApiResponse())) {
                                    return sendMessage(telegramId, boxAnswer, saveMessageId);
                                }
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage(), e);
                            }
                            return Uni.createFrom().nullItem();
                        }
                );
    }

    private Uni<SentBox<Integer>> sendMessage(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transform(
                        v -> {
                            final SendMessage sendMessage = new SendMessage();
                            sendMessage.enableMarkdown(true);
                            sendMessage.setChatId(telegramId);
                            sendMessage.setText(boxAnswer.getMessage());
                            sendMessage.setReplyMarkup(KeyBoardConvert.convertKeyBoard(boxAnswer.getKeyBoard()));

                            try {
                                return absSender.execute(sendMessage);
                            } catch (TelegramApiRequestException e) {
                                log.error(e.getApiResponse());
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage());
                            }
                            return null;
                        }
                ).onItem().ifNotNull().call(answerMessage -> {
                    if (checkNotNull(senderRepository) && saveMessageId) {
                        return senderRepository.saveLastSendMessage(telegramId, answerMessage.getMessageId());
                    }
                    return null;
                })
                .onItem().ifNotNull().transformToUni(
                        answerMessage -> Uni.createFrom().optional(SentBox.optional(answerMessage.getMessageId(), boxAnswer, boxAnswer))
                );
    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
