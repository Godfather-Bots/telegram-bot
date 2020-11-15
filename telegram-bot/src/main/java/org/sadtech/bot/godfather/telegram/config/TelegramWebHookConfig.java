package org.sadtech.bot.godfather.telegram.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [12.02.2020]
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramWebHookConfig {

    @NonNull
    private String botUsername;

    @NonNull
    private String botToken;

}
