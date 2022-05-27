package dev.struchkov.godfather.telegram.listen;

import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.button.SimpleButton;
import dev.struchkov.godfather.context.domain.keyboard.simple.SimpleKeyBoard;
import dev.struchkov.godfather.context.service.sender.SendType;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.MarkupKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.button.ButtonUrl;
import dev.struchkov.godfather.telegram.service.SendPreProcessing;
import dev.struchkov.haiti.context.exception.ConvertException;
import dev.struchkov.haiti.utils.Inspector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
public class TelegramSender implements Sending {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private static final String ERROR_REPLACE_MESSAGE = "Bad Request: message to edit not found";

    private final AbsSender absSender;
    private Map<Long, Integer> map = new HashMap<>();

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
            if (boxAnswer.isReplace() && map.containsKey(telegramId)) {
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
        editMessageText.setMessageId(map.get(telegramId));
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

            map.put(telegramId, execute.getMessageId());
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private ReplyKeyboard convertKeyBoard(KeyBoard keyBoard) {
        if (keyBoard != null) {
            switch (keyBoard.getType()) {
                case InlineKeyBoard.TYPE:
                    return convertInlineKeyBoard((InlineKeyBoard) keyBoard);
                case MarkupKeyBoard.TYPE:
                    return convertMarkupKeyBoard((MarkupKeyBoard) keyBoard);
                case SimpleKeyBoard.TYPE:
                    return convertSimpleKeyBoard((SimpleKeyBoard) keyBoard);
            }
        }
        return null;
    }

    private ReplyKeyboard convertSimpleKeyBoard(SimpleKeyBoard keyBoard) {
        final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(
                keyBoard.getLines().stream()
                        .map(this::convertMarkupLine)
                        .toList()
        );
        return keyboardMarkup;
    }

    private ReplyKeyboard convertMarkupKeyBoard(MarkupKeyBoard keyBoard) {
        if (keyBoard != null) {
            final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setOneTimeKeyboard(keyBoard.isOneTime());
            keyboardMarkup.setInputFieldPlaceholder(keyBoard.getInputFieldPlaceholder());
            keyboardMarkup.setResizeKeyboard(keyBoard.isResizeKeyboard());
            keyboardMarkup.setKeyboard(
                    keyBoard.getLines().stream()
                            .map(this::convertMarkupLine)
                            .toList()
            );
            return keyboardMarkup;
        }
        return null;
    }

    private InlineKeyboardMarkup convertInlineKeyBoard(InlineKeyBoard keyBoard) {
        if (keyBoard != null) {
            final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(
                    keyBoard.getLines().stream()
                            .map(this::convertInlineLine)
                            .toList()
            );
            return inlineKeyboardMarkup;
        }
        return null;
    }

    private List<InlineKeyboardButton> convertInlineLine(KeyBoardLine line) {
        return line.getButtons().stream().map(this::convertInlineButton).toList();
    }

    private KeyboardRow convertMarkupLine(KeyBoardLine line) {
        final List<KeyboardButton> buttons = line.getButtons().stream().map(this::convertMarkupButton).toList();
        return new KeyboardRow(buttons);
    }

    private InlineKeyboardButton convertInlineButton(KeyBoardButton keyBoardButton) {
        final InlineKeyboardButton button = new InlineKeyboardButton();
        switch (keyBoardButton.getType()) {
            case SimpleButton.TYPE -> {
                final SimpleButton simpleButton = (SimpleButton) keyBoardButton;
                final String callbackData = simpleButton.getCallbackData();
                final String label = simpleButton.getLabel();
                button.setText(label);
                button.setCallbackData(callbackData != null ? callbackData : label);
            }
            case ButtonUrl.TYPE -> {
                final ButtonUrl buttonUrl = (ButtonUrl) keyBoardButton;
                button.setUrl(buttonUrl.getUrl());
                button.setText(buttonUrl.getLabel());
            }
            default -> throw new ConvertException("Ошибка преобразования кнопки");
        }
        return button;
    }

    private KeyboardButton convertMarkupButton(KeyBoardButton keyBoardButton) {
        final KeyboardButton button = new KeyboardButton();
        switch (keyBoardButton.getType()) {
            case SimpleButton.TYPE -> {
                final SimpleButton simpleButton = (SimpleButton) keyBoardButton;
                button.setText(simpleButton.getLabel());
                Inspector.isNull(simpleButton.getCallbackData(), ConvertException.supplier("CallbackData поддерживает только Inline клавитаура"));
            }
            default -> throw new ConvertException("Ошибка преобразования кнопки");
        }
        return button;
    }

    public void send(Long integer, Long integer1, BoxAnswer boxAnswer) {

    }

    public SendType getType() {
        return SendType.PRIVATE;
    }

}
