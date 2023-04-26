package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static dev.struchkov.haiti.utils.Inspector.Utils.nullPointer;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Запрашивает у пользователя его контактный номер.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContactButton implements KeyBoardButton {

    public static final String TYPE = "CONTACT";

    private String label;

    public static ContactButton contactButton(String label) {
        isNotNull(label, nullPointer("label"));
        return new ContactButton(label);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
