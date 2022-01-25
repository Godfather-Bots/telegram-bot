package dev.struchkov.godfather.telegram.config;

import dev.struchkov.godfather.telegram.ProxyConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramPollingConfig {

    @NonNull
    private String botUsername;

    @NonNull
    private String botToken;

    private ProxyConfig proxyConfig = new ProxyConfig();

}
