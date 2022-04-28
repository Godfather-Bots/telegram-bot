package dev.struchkov.godfather.telegram.config;

import dev.struchkov.godfather.telegram.ProxyConfig;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class TelegramPollingConfig {

    private final String botUsername;
    private final String botToken;

    private ProxyConfig proxyConfig;

    public TelegramPollingConfig(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

}
