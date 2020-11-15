package org.sadtech.bot.godfather.telegram.listen;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.godfather.telegram.service.SendPreProcessing;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.keyboard.KeyBoard;
import org.sadtech.social.core.domain.keyboard.KeyBoardButton;
import org.sadtech.social.core.domain.keyboard.KeyBoardLine;
import org.sadtech.social.core.domain.keyboard.button.KeyBoardButtonText;
import org.sadtech.social.core.service.sender.SendType;
import org.sadtech.social.core.service.sender.Sending;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
@Slf4j
public class TelegramSender implements Sending {

    private final AbsSender absSender;

    @Setter
    private SendPreProcessing sendPreProcessing;

    public TelegramSender(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAdsSender();
    }

    public void send(Long integer, BoxAnswer boxAnswer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(integer));
        sendMessage.setText(
                sendPreProcessing != null
                        ? sendPreProcessing.pretreatment(boxAnswer.getMessage())
                        : boxAnswer.getMessage()
        );
        sendMessage.setReplyMarkup(convertKeyBoard(boxAnswer.getKeyBoard()));
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private ReplyKeyboard convertKeyBoard(KeyBoard keyBoard) {
        if (keyBoard != null) {
            final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(
                    keyBoard.getKeyBoardLines()
                            .stream()
                            .map(this::convertLint)
                            .collect(Collectors.toList())
            );
            return inlineKeyboardMarkup;
        }
        return null;
    }

    private List<InlineKeyboardButton> convertLint(KeyBoardLine line) {
        return line.getKeyBoardButtons().stream().map(this::convertButton).collect(Collectors.toList());
    }

    private InlineKeyboardButton convertButton(KeyBoardButton button) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        switch (button.getType()) {
            case TEXT:
                KeyBoardButtonText keyBoardButtonText = (KeyBoardButtonText) button;
                inlineKeyboardButton.setText(keyBoardButtonText.getLabel());
                inlineKeyboardButton.setCallbackData(keyBoardButtonText.getLabel());
        }
        return inlineKeyboardButton;
    }

    public void send(Long integer, Long integer1, BoxAnswer boxAnswer) {

    }

    public SendType getType() {
        return SendType.PRIVATE;
    }

}
