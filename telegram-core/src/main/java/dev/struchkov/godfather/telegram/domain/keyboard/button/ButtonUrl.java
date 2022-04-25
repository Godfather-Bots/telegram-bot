package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ButtonUrl implements KeyBoardButton {

    public static final String TYPE = "URL";

    private String label;
    private String url;

    @Override
    public String getType() {
        return TYPE;
    }

}
