package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import org.jetbrains.annotations.NotNull;

/**
 * Запрашивает у пользователя его контактный номер.
 */
public class ContactButton implements KeyBoardButton {

    public static final String TYPE = "CONTACT";

    private final String label;

    private ContactButton(String label) {
        this.label = label;
    }

    public static ContactButton contactButton(@NotNull String label) {
        return new ContactButton(label);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
