package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;

/**
 * Запрашивает у пользователя его контактный номер.
 */
public class ContactButton implements KeyBoardButton {

    public static final String TYPE = "CONTACT";

    private final String label;

    public ContactButton(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
