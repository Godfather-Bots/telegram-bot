package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;

public class WebAppButton implements KeyBoardButton {

    public static final String TYPE = "WEB_APP";

    private final String label;
    private final String url;

    public WebAppButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static WebAppButton buttonWebApp(String label, String url) {
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
