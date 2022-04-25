package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.simple.SimpleKeyBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class MarkupKeyBoard extends SimpleKeyBoard {

    public static final String TYPE = "MARKUP";

    /**
     * Скрыть меню после ответа или нет.
     */
    private boolean oneTime = true;

    /**
     * Изменяет размер клавиатуры по вертикали для оптимального соответствия (например, сделать клавиатуру меньше, если есть только два ряда кнопок).
     */
    private boolean resizeKeyboard;

    private String inputFieldPlaceholder;

    @Builder
    protected MarkupKeyBoard(
            @Singular(value = "line") List<KeyBoardLine> keyBoardLines,
            boolean oneTime,
            boolean resizeKeyboard,
            String inputFieldPlaceholder
    ) {
        super(keyBoardLines);
        this.oneTime = oneTime;
        this.resizeKeyboard = resizeKeyboard;
        this.inputFieldPlaceholder = inputFieldPlaceholder;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
