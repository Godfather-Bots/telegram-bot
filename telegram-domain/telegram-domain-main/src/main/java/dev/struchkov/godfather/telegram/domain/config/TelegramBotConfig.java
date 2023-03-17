package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
@Getter
@Setter
@NoArgsConstructor
public class TelegramBotConfig {

    private String username;
    private String token;
    private String webHookUrl;

    private ProxyConfig proxyConfig;

    public TelegramBotConfig(String username, String token) {
        this.username = username;
        this.token = token;
    }

}
