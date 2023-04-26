package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonArg;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Inspector.Utils.nullPointer;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Абстрактная сущность кнопки для клавиатуры.
 *
 * @author upagge [08/07/2019]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleButton implements KeyBoardButton {

    public static final String TYPE = "SIMPLE";

    /**
     * Надпись на кнопке.
     */
    protected String label;
    /**
     * Данные, которые возвращаются при нажатии.
     */
    protected String callbackData;

    protected SimpleButton(String label, String callbackData) {
        this.label = label;
        this.callbackData = callbackData;
    }

    public static SimpleButton simpleButton(String label, String callbackData) {
        isNotNull(label, nullPointer("label"));
        return new SimpleButton(label, callbackData);
    }

    public static SimpleButton simpleButton(String label, ButtonArg... args) {
        isNotNull(label, nullPointer("label"));
        return new SimpleButton(
                label, Arrays.stream(args)
                .map(buttonArg -> buttonArg.getType() + ":" + buttonArg.getValue())
                .collect(Collectors.joining(";", "[", "]"))
        );
    }

    public static SimpleButton simpleButton(String label) {
        isNotNull(label, nullPointer("label"));
        return new SimpleButton(label, null);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
