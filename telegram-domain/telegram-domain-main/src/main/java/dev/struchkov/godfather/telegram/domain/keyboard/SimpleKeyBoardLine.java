package dev.struchkov.godfather.telegram.domain.keyboard;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

import java.util.Arrays;
import java.util.List;

/**
 * Строка в меню клавиатуры {@link dev.struchkov.godfather.main.domain.keyboard.KeyBoard}.
 *
 * @author upagge [08/07/2019]
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleKeyBoardLine implements KeyBoardLine {

    /**
     * Кнопки в строке.
     */
    @Singular
    protected List<KeyBoardButton> buttons;

    public static SimpleKeyBoardLine keyBoardLine(KeyBoardButton... keyBoardButton) {
        return new SimpleKeyBoardLine(Arrays.stream(keyBoardButton).toList());
    }

}
