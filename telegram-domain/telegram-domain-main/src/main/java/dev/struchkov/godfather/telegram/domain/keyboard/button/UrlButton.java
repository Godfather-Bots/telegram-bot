package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

@Getter
@Setter
@NoArgsConstructor
public class UrlButton implements KeyBoardButton {

    public static final String TYPE = "URL";

    private String label;
    private String url;

    public UrlButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static UrlButton urlButton(String label, String url) {
        isNotNull(label, url);
        return new UrlButton(label, url);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
