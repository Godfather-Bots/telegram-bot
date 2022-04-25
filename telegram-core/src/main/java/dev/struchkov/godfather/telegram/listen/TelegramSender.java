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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
@Slf4j
public class TelegramSender implements Sending {

    private final AbsSender absSender;
    private Map<Long, Integer> map = new HashMap<>();
    @Setter
    private SendPreProcessing sendPreProcessing;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAdsSender();
    }

    public void send(Long telegramId, BoxAnswer boxAnswer) {
        try {
            if (boxAnswer.isReplace() && map.containsKey(telegramId)) {
                final EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(telegramId));
                editMessageText.setMessageId(map.get(telegramId));
                editMessageText.enableMarkdown(true);
                editMessageText.setText(boxAnswer.getMessage());
                editMessageText.setReplyMarkup(convertInlineKeyBoard((InlineKeyBoard) boxAnswer.getKeyBoard()));
                absSender.execute(editMessageText);
            } else {
                final SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(String.valueOf(telegramId));
                sendMessage.setText(
                        sendPreProcessing != null
                                ? sendPreProcessing.pretreatment(boxAnswer.getMessage())
                                : boxAnswer.getMessage()
                );
                sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));

                final Message execute = absSender.execute(sendMessage);
                map.put(telegramId, execute.getMessageId());
            }
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
        throw new RuntimeException("Ошибка преобразования клавиаутры");
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

    private InlineKeyboardMarkup convertInlineKeyBoard(InlineKeyBoard keyBoard) {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(
                keyBoard.getLines().stream()
                        .map(this::convertInlineLine)
                        .toList()
        );
        return inlineKeyboardMarkup;
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
            default -> throw new RuntimeException("Ошибка преобразования кнопки");
        }
        return button;
    }

    private KeyboardButton convertMarkupButton(KeyBoardButton keyBoardButton) {
        final KeyboardButton button = new KeyboardButton();
        switch (keyBoardButton.getType()) {
            case SimpleButton.TYPE -> {
                final SimpleButton simpleButton = (SimpleButton) keyBoardButton;
                button.setText(simpleButton.getLabel());
            }
            default -> throw new RuntimeException("Ошибка преобразования кнопки");
        }
        return button;
    }

    public void send(Long integer, Long integer1, BoxAnswer boxAnswer) {

    }

    public SendType getType() {
        return SendType.PRIVATE;
    }

}
