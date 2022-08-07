package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;

import java.util.ArrayList;
import java.util.List;

import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;

public class InlineKeyBoard implements KeyBoard {

    public static final String TYPE = "INLINE";

    protected List<KeyBoardLine> lines;

    public InlineKeyBoard(List<KeyBoardLine> lines) {
        this.lines = lines;
    }

    private InlineKeyBoard(Builder builder) {
        this.lines = builder.lines;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static InlineKeyBoard inlineKeyBoard(KeyBoardLine... keyBoardLine) {
        final Builder builder = builder();
        for (KeyBoardLine boardLine : keyBoardLine) {
            builder.line(boardLine);
        }
        return builder.build();
    }

    public static InlineKeyBoard inlineKeyBoard(KeyBoardButton... buttons) {
        return builder().line(simpleLine(buttons)).build();
    }

    public List<KeyBoardLine> getLines() {
        return lines;
    }

    public String getType() {
        return TYPE;
    }

    public static final class Builder {
        private List<KeyBoardLine> lines = new ArrayList<>();

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

        public InlineKeyBoard build() {
            return new InlineKeyBoard(this);
        }
    }

}
