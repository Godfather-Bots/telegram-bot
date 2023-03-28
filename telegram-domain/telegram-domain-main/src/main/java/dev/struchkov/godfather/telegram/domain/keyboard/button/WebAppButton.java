package dev.struchkov.godfather.telegram.domain.keyboard.button;

import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WebAppButton implements KeyBoardButton {

    public static final String TYPE = "WEB_APP";

    private String label;
    private String url;

    public static WebAppButton webAppButton(String label, String url) {
        isNotNull(label, url);
        return new WebAppButton(label, url);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
