package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;

import static dev.struchkov.haiti.utils.Inspector.Utils.nullPointer;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Запрашивает у пользователя его контактный номер.
 */
public class ContactButton implements KeyBoardButton {

    public static final String TYPE = "CONTACT";

    private final String label;

    private ContactButton(String label) {
        this.label = label;
    }

    public static ContactButton contactButton(String label) {
        isNotNull(label, nullPointer("label"));
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
