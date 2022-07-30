package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;
import org.jetbrains.annotations.NotNull;

public class WebAppButton implements KeyBoardButton {

    public static final String TYPE = "WEB_APP";

    private final String label;
    private final String url;

    private WebAppButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static WebAppButton webAppButton(@NotNull String label, @NotNull String url) {
        return new WebAppButton(label, url);
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
