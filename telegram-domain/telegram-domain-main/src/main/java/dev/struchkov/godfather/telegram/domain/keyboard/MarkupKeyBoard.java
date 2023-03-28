package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkupKeyBoard implements KeyBoard {

    public static final String TYPE = "MARKUP";

    private static final MarkupKeyBoard EMPTY = new MarkupKeyBoard();

    @Singular
    protected List<KeyBoardLine> lines;

    /**
     * Скрыть меню после ответа или нет.
     */
    private boolean oneTime;

    /**
     * Изменяет размер клавиатуры по вертикали для оптимального соответствия (например, сделать клавиатуру меньше, если есть только два ряда кнопок).
     */
    private boolean resizeKeyboard;

    private String inputFieldPlaceholder;

    public static MarkupKeyBoard markupKeyBoard(KeyBoardLine... lines) {
        final MarkupKeyBoardBuilder builder = new MarkupKeyBoardBuilder();
        for (KeyBoardLine line : lines) {
            builder.line(line);
        }
        return builder.build();
    }

    public static MarkupKeyBoard markupKeyBoard(KeyBoardButton... buttons) {
        final MarkupKeyBoardBuilder builder = new MarkupKeyBoardBuilder();
        for (KeyBoardButton button : buttons) {
            builder.line(simpleLine(button));
        }
        return builder.build();
    }

    public static MarkupKeyBoard empty() {
        return EMPTY;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public boolean isNotEmpty() {
        return !lines.isEmpty();
    }

}
