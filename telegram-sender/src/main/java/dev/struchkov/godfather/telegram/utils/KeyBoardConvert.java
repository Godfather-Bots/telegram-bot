package dev.struchkov.godfather.telegram.utils;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.button.SimpleButton;
import dev.struchkov.godfather.context.domain.keyboard.simple.SimpleKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.MarkupKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.button.ContactButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.WebAppButton;
import dev.struchkov.haiti.context.exception.ConvertException;
import dev.struchkov.haiti.utils.Exceptions;
import dev.struchkov.haiti.utils.Inspector;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

import static dev.struchkov.haiti.context.exception.ConvertException.convertException;
import static dev.struchkov.haiti.utils.Inspector.isNull;

public final class KeyBoardConvert {

    private KeyBoardConvert() {
        Exceptions.utilityClass();
    }

    public static ReplyKeyboard convertKeyBoard(KeyBoard keyBoard) {
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

    public static ReplyKeyboard convertSimpleKeyBoard(SimpleKeyBoard keyBoard) {
        final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(
                keyBoard.getLines().stream()
                        .map(KeyBoardConvert::convertMarkupLine)
                        .toList()
        );
        return keyboardMarkup;
    }

    public static ReplyKeyboard convertMarkupKeyBoard(MarkupKeyBoard keyBoard) {
        if (keyBoard != null) {
            if (keyBoard.isNotEmpty()) {
                final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                keyboardMarkup.setOneTimeKeyboard(keyBoard.isOneTime());
                keyboardMarkup.setInputFieldPlaceholder(keyBoard.getInputFieldPlaceholder());
                keyboardMarkup.setResizeKeyboard(keyBoard.isResizeKeyboard());
                keyboardMarkup.setKeyboard(
                        keyBoard.getLines().stream()
                                .map(KeyBoardConvert::convertMarkupLine)
                                .toList()
                );
                return keyboardMarkup;
            } else {
                final ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                replyKeyboardRemove.setRemoveKeyboard(true);
                return replyKeyboardRemove;
            }
        }
        return null;
    }

    public static InlineKeyboardMarkup convertInlineKeyBoard(InlineKeyBoard keyBoard) {
        if (keyBoard != null) {
            final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(
                    keyBoard.getLines().stream()
                            .map(KeyBoardConvert::convertInlineLine)
                            .toList()
            );
            return inlineKeyboardMarkup;
        }
        return null;
    }

    private static List<InlineKeyboardButton> convertInlineLine(KeyBoardLine line) {
        return line.getButtons().stream().map(KeyBoardConvert::convertInlineButton).toList();
    }

    private static KeyboardRow convertMarkupLine(KeyBoardLine line) {
        final List<KeyboardButton> buttons = line.getButtons().stream().map(KeyBoardConvert::convertMarkupButton).toList();
        return new KeyboardRow(buttons);
    }

    private static InlineKeyboardButton convertInlineButton(KeyBoardButton keyBoardButton) {
        final InlineKeyboardButton button = new InlineKeyboardButton();
        switch (keyBoardButton.getType()) {
            case SimpleButton.TYPE -> {
                final SimpleButton simpleButton = (SimpleButton) keyBoardButton;
                final String callbackData = simpleButton.getCallbackData();
                final String label = simpleButton.getLabel();
                button.setText(label);
                button.setCallbackData(callbackData != null ? callbackData : label);
            }
            case UrlButton.TYPE -> {
                final UrlButton urlButton = (UrlButton) keyBoardButton;
                button.setUrl(urlButton.getUrl());
                button.setText(urlButton.getLabel());
            }
            case WebAppButton.TYPE -> {
                final WebAppButton webAppButton = (WebAppButton) keyBoardButton;
                final WebAppInfo webAppInfo = WebAppInfo.builder().url(webAppButton.getUrl()).build();
                button.setWebApp(webAppInfo);
                button.setText(webAppButton.getLabel());
            }
            default -> throw new ConvertException("Ошибка преобразования кнопки");
        }
        return button;
    }

    private static KeyboardButton convertMarkupButton(KeyBoardButton keyBoardButton) {
        final KeyboardButton button = new KeyboardButton();
        switch (keyBoardButton.getType()) {
            case SimpleButton.TYPE -> {
                final SimpleButton simpleButton = (SimpleButton) keyBoardButton;
                button.setText(simpleButton.getLabel());
                isNull(simpleButton.getCallbackData(), convertException("CallbackData поддерживает только Inline клавитаура"));
            }
            case WebAppButton.TYPE -> {
                final WebAppButton webAppButton = (WebAppButton) keyBoardButton;
                final WebAppInfo webAppInfo = WebAppInfo.builder().url(webAppButton.getUrl()).build();
                button.setText(webAppButton.getLabel());
                button.setWebApp(webAppInfo);
            }
            case ContactButton.TYPE -> {
                final ContactButton contactButton = (ContactButton) keyBoardButton;

                button.setText(contactButton.getLabel());
                button.setRequestContact(true);
            }
            default -> throw new ConvertException("Ошибка преобразования кнопки");
        }
        return button;
    }

}
