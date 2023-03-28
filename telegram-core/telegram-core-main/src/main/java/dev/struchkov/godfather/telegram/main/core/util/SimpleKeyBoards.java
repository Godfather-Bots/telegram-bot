package dev.struchkov.godfather.telegram.main.core.util;

import dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton;
import dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine;
import dev.struchkov.godfather.telegram.domain.keyboard.MarkupKeyBoard;

import java.util.Arrays;
import java.util.List;

/**
 * Используется для быстрого создания клавиаутр {@link MarkupKeyBoard}.
 *
 * @author upagge [08/07/2019]
 */
public class SimpleKeyBoards {

    public static final SimpleButton YES_BUTTON = SimpleButton.simpleButton("Да", "{\"button\": \"yes\"}");
    public static final SimpleButton NO_BUTTON = SimpleButton.simpleButton("Нет", "{\"button\": \"no\"}");

    private SimpleKeyBoards() {
        throw new IllegalStateException();
    }

    /**
     * Возвращает клавиатуру формата 1х2, с кнопками "Да | Нет"
     *
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard keyBoardYesNo() {
        return MarkupKeyBoard.builder().line(
                SimpleKeyBoardLine.builder().button(YES_BUTTON).button(NO_BUTTON).build()
        ).build();
    }

    /**
     * Возвращает клавиатуру формата 1хN, где N - это количество элементов в переданном списке
     *
     * @param labelButtons Список названий для кнопок
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard verticalMenuString(List<String> labelButtons) {
        final MarkupKeyBoard.MarkupKeyBoardBuilder keyBoard = MarkupKeyBoard.builder();
        for (String labelButton : labelButtons) {
            final SimpleButton simpleButton = SimpleButton.simpleButton(labelButton, "{\"button\": \"" + labelButton + "\"}");
            keyBoard.line(SimpleKeyBoardLine.builder().button(simpleButton).build());
        }
        return keyBoard.build();
    }

    /**
     * Возвращает клавиатуру формата 1хN, где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard verticalMenuString(String... labelButton) {
        return verticalMenuString(Arrays.asList(labelButton));
    }

    /**
     * Возвращает клавиатуру формата 2х(N/2), где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard verticalDuoMenuString(String... labelButton) {
        return verticalDuoMenuString(Arrays.asList(labelButton));
    }

    /**
     * Возвращает клавиатуру формата 2х(N/2), где N - это количество элементов в переданном списке
     *
     * @param labelButton Список названий для кнопок
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard verticalDuoMenuString(List<String> labelButton) {
        final MarkupKeyBoard.MarkupKeyBoardBuilder keyBoard = MarkupKeyBoard.builder();
        boolean flag = true;
        SimpleKeyBoardLine.SimpleKeyBoardLineBuilder keyBoardLine = SimpleKeyBoardLine.builder();
        for (int i = 0; i <= labelButton.size() - 1; i++) {
            String label = labelButton.get(i);
            keyBoardLine.button(SimpleButton.simpleButton(label));
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

    /**
     * Возвращает клавиатуру формата 1xN сформированную из списка кнопок, где N - количество кнопок в списке
     *
     * @param simpleButtons Список кнопок
     * @return {@link MarkupKeyBoard}
     */
    public static MarkupKeyBoard verticalMenuButton(List<SimpleButton> simpleButtons) {
        final MarkupKeyBoard.MarkupKeyBoardBuilder keyBoard = MarkupKeyBoard.builder();
        for (SimpleButton simpleButton : simpleButtons) {
            keyBoard.line(SimpleKeyBoardLine.builder().button(simpleButton).build());
        }
        return keyBoard.build();
    }

}
