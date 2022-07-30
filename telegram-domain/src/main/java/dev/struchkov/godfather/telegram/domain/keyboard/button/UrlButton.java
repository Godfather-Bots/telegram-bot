package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import org.jetbrains.annotations.NotNull;

public class UrlButton implements KeyBoardButton {

    public static final String TYPE = "URL";

    private final String label;
    private final String url;

    public UrlButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static UrlButton urlButton(@NotNull String label, @NotNull String url) {
        return new UrlButton(label, url);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}
