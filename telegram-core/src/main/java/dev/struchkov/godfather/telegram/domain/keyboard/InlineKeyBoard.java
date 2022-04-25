package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.simple.SimpleKeyBoard;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

public class InlineKeyBoard extends SimpleKeyBoard {

    public static final String TYPE = "INLINE";

    @Builder
    public InlineKeyBoard(@Singular("line") List<KeyBoardLine> lines) {
        super(lines);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
