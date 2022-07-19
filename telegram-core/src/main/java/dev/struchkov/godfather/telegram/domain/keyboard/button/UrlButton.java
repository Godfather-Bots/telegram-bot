package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.context.domain.keyboard.KeyBoardButton;

public class UrlButton implements KeyBoardButton {

    public static final String TYPE = "URL";

    private String label;
    private String url;

    public UrlButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static UrlButton buttonUrl(String label, String url) {
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