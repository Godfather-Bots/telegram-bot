package dev.struchkov.godfather.telegram.quarkus.sender;

import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.SendType;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import static dev.struchkov.godfather.telegram.main.sender.util.KeyBoardConvert.convertInlineKeyBoard;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class TelegramSender implements TelegramSending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;

    private SendPreProcessing sendPreProcessing;
    private SenderRepository senderRepository;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    public TelegramSender(TelegramConnect telegramConnect, SenderRepository senderRepository) {
        this.absSender = telegramConnect.getAbsSender();
        this.senderRepository = senderRepository;
    }

    public void setSendPreProcessing(SendPreProcessing sendPreProcessing) {
        this.sendPreProcessing = sendPreProcessing;
    }

    public void setSenderRepository(SenderRepository senderStorageService) {
        this.senderRepository = senderRepository;
    }

    @Override
    public Uni<Void> send(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(telegramId, boxAnswer, true);
    }

    @Override
    public Uni<Void> sendNotSave(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer) {
        return sendBoxAnswer(telegramId, boxAnswer, false);
    }

    private Uni<Void> sendBoxAnswer(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            isNotNull(telegramId, boxAnswer);

                            if (boxAnswer.isReplace() && checkNotNull(senderRepository)) {
                                return senderRepository.getLastSendMessage(telegramId)
                                        .onItem().transformToUni(
                                                lastId -> {
                                                    if (checkNotNull(lastId)) {
                                                        return replaceMessage(telegramId, lastId, boxAnswer);
                                                    } else {
                                                        return sendMessage(telegramId, boxAnswer, saveMessageId);
                                                    }
                                                }
                                        );
                            } else {
                                return sendMessage(telegramId, boxAnswer, saveMessageId);
                            }
                        }
                );
    }

    private Uni<Void> replaceMessage(@NotNull String telegramId, @NotNull Integer lastMessageId, @NotNull BoxAnswer boxAnswer) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final EditMessageText editMessageText = new EditMessageText();
                            editMessageText.setChatId(String.valueOf(telegramId));
                            editMessageText.setMessageId(lastMessageId);
                            editMessageText.enableMarkdown(true);
                            editMessageText.setText(boxAnswer.getMessage());
                            editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
                            try {
                                absSender.execute(editMessageText);
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage());
                            }
                            return Uni.createFrom().voidItem();
                        }
                );
    }

    private Uni<Void> sendMessage(@NotNull String telegramId, @NotNull BoxAnswer boxAnswer, boolean saveMessageId) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final SendMessage sendMessage = new SendMessage();
                            sendMessage.enableMarkdown(true);
                            sendMessage.setChatId(telegramId);
                            sendMessage.setText(
                                    sendPreProcessing != null
                                            ? sendPreProcessing.pretreatment(boxAnswer.getMessage())
                                            : boxAnswer.getMessage()
                            );
                            sendMessage.setReplyMarkup(KeyBoardConvert.convertKeyBoard(boxAnswer.getKeyBoard()));

                            try {
                                final Message execute = absSender.execute(sendMessage);
                                if (checkNotNull(senderRepository) && saveMessageId) {
                                    return senderRepository.saveLastSendMessage(telegramId, execute.getMessageId());
                                }
                            } catch (TelegramApiRequestException e) {
                                log.error(e.getApiResponse());
                                if (ERROR_REPLACE_MESSAGE.equals(e.getApiResponse())) {
                                    return sendMessage(telegramId, boxAnswer, saveMessageId);
                                }
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage());
                            }
                            return Uni.createFrom().voidItem();
                        }
                ).replaceWithVoid();
    }

    @Override
    public SendType getType() {
        return SendType.PRIVATE;
    }

}
