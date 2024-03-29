package dev.struchkov.godfather.telegram.main.core.util;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton;
import dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoard;
import dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;

import java.util.Arrays;
import java.util.List;

import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class InlineKeyBoards {

    public static final SimpleButton YES_BUTTON = simpleButton("Да", "Да");
    public static final SimpleButton NO_BUTTON = simpleButton("Нет", "Нет");

    private InlineKeyBoards() {
        utilityClass();
    }

    /**
     * Возвращает клавиатуру формата 1х2, с кнопками "Да | Нет"
     *
     * @return {@link SimpleKeyBoard}
     */
    public static SimpleKeyBoardLine lineYesOrNo() {
        return simpleLine(YES_BUTTON, NO_BUTTON);
    }

    /**
     * Возвращает клавиатуру формата 1хN, где N - это количество элементов в переданном списке
     *
     * @param labelButtons Список названий для кнопок
     * @return {@link SimpleKeyBoard}
     */
    public static InlineKeyBoard verticalMenuString(List<String> labelButtons) {
        final InlineKeyBoard.Builder keyBoard = InlineKeyBoard.builder();
        for (String labelButton : labelButtons) {
            keyBoard.line(simpleLine(simpleButton(labelButton, labelButton)));
        }
        return keyBoard.build();
    }

    /**
     * Возвращает клавиатуру формата 1хN, где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link SimpleKeyBoard}
     */
    public static InlineKeyBoard verticalMenuString(String... labelButton) {
        return verticalMenuString(Arrays.asList(labelButton));
    }

    /**
     * Возвращает клавиатуру формата 2х(N/2), где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link SimpleKeyBoard}
     */
    public static InlineKeyBoard verticalDuoMenuString(String... labelButton) {
        return verticalDuoMenuString(Arrays.asList(labelButton));
    }

    /**
     * Возвращает клавиатуру формата 2х(N/2), где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link SimpleKeyBoard}
     */
    public static InlineKeyBoard verticalDuoMenuString(List<String> labelButton) {
        final InlineKeyBoard.Builder keyBoard = InlineKeyBoard.builder();
        boolean flag = true;
        SimpleKeyBoardLine.Builder keyBoardLine = SimpleKeyBoardLine.builder();
        for (int i = 0; i <= labelButton.size() - 1; i++) {
            String label = labelButton.get(i);
            keyBoardLine.button(simpleButton(label));
            if (flag) {
                if (i == labelButton.size() - 1) {
                    keyBoard.line(keyBoardLine.build());
                } else {
                    flag = false;
                }
            } else {
                keyBoard.line(keyBoardLine.build());
                keyBoardLine = SimpleKeyBoardLine.builder();
                flag = true;
            }
        }
        return keyBoard.build();
    }

    public static void verticalDuoMenu(InlineKeyBoard.Builder builder, List<? extends KeyBoardButton> buttons) {
        boolean flag = true;
        SimpleKeyBoardLine.Builder keyBoardLine = SimpleKeyBoardLine.builder();
        for (int i = 0; i <= buttons.size() - 1; i++) {
            keyBoardLine.button(buttons.get(i));
            if (flag) {
                if (i == buttons.size() - 1) {
                    builder.line(keyBoardLine.build());
                } else {
                    flag = false;
                }
            } else {
                builder.line(keyBoardLine.build());
                keyBoardLine = SimpleKeyBoardLine.builder();
                flag = true;
            }
        }
    }

    public static InlineKeyBoard verticalDuoMenu(List<? extends KeyBoardButton> buttons) {
        final InlineKeyBoard.Builder keyBoard = InlineKeyBoard.builder();
        verticalDuoMenu(keyBoard, buttons);
        return keyBoard.build();
    }

    public static InlineKeyBoard verticalDuoMenu(KeyBoardButton... buttons) {
        return verticalDuoMenu(Arrays.stream(buttons).toList());
    }

    /**
     * Возвращает клавиатуру формата 1xN сформированную из списка кнопок, где N - количество кнопок в списке
     *
     * @param buttons Список кнопок
     * @return {@link SimpleKeyBoard}
     */
    public static InlineKeyBoard verticalMenuButton(KeyBoardButton... buttons) {
        final InlineKeyBoard.Builder keyBoard = InlineKeyBoard.builder();
        for (KeyBoardButton simpleButton : buttons) {
            keyBoard.line(simpleLine(simpleButton));
        }
        return keyBoard.build();
    }

}
