package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.simple.SimpleKeyBoard;

import java.util.ArrayList;
import java.util.List;

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

    private MarkupKeyBoard(Builder builder) {
        super(builder.lines);
        oneTime = builder.oneTime;
        resizeKeyboard = builder.resizeKeyboard;
        inputFieldPlaceholder = builder.inputFieldPlaceholder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean isResizeKeyboard() {
        return resizeKeyboard;
    }

    public String getInputFieldPlaceholder() {
        return inputFieldPlaceholder;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Builder {
        private List<KeyBoardLine> lines = new ArrayList<>();
        private boolean oneTime = true;
        private boolean resizeKeyboard;
        private String inputFieldPlaceholder;

        private Builder() {
        }

        public Builder lines(List<KeyBoardLine> val) {
            lines = val;
            return this;
        }

        public Builder line(KeyBoardLine val) {
            lines.add(val);
            return this;
        }

        public Builder oneTime(boolean val) {
            oneTime = val;
            return this;
        }

        public Builder resizeKeyboard(boolean val) {
            resizeKeyboard = val;
            return this;
        }

        public Builder inputFieldPlaceholder(String val) {
            inputFieldPlaceholder = val;
            return this;
        }

        public MarkupKeyBoard build() {
            return new MarkupKeyBoard(this);
        }
    }
}
