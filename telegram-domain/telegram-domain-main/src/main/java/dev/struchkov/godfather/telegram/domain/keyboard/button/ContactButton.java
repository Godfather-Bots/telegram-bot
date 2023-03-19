package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static dev.struchkov.haiti.utils.Inspector.Utils.nullPointer;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Запрашивает у пользователя его контактный номер.
 */
@Getter
@Setter
@NoArgsConstructor
public class ContactButton implements KeyBoardButton {

    public static final String TYPE = "CONTACT";

    private String label;

    private ContactButton(String label) {
        this.label = label;
    }

    public static ContactButton contactButton(String label) {
        isNotNull(label, nullPointer("label"));
        return new ContactButton(label);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
