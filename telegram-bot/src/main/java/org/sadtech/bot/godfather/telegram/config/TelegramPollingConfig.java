package org.sadtech.bot.godfather.telegram.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.sadtech.bot.godfather.telegram.ProxyConfig;

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
