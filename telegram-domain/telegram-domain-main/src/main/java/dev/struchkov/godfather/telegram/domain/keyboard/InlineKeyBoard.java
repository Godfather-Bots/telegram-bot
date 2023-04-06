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

import static dev.struchkov.godfather.telegram.domain.keyboard.SimpleKeyBoardLine.keyBoardLine;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InlineKeyBoard implements KeyBoard {

    public static final String TYPE = "INLINE";

    @Singular
    protected List<KeyBoardLine> lines;

    public static InlineKeyBoard inlineKeyBoard(KeyBoardLine... keyBoardLine) {
        final InlineKeyBoardBuilder builder = builder();
        for (KeyBoardLine boardLine : keyBoardLine) {
            builder.line(boardLine);
        }
        return builder.build();
    }

    public static InlineKeyBoard inlineKeyBoard(KeyBoardButton... buttons) {
        return builder().line(keyBoardLine(buttons)).build();
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
