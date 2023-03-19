package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TelegramBotConfig {

    private String username;

    @ToString.Exclude
    private String token;

    private ProxyConfig proxyConfig;
    private WebhookConfig webhookConfig;

    public TelegramBotConfig(String username, String token) {
        this.username = username;
        this.token = token;
    }

}
