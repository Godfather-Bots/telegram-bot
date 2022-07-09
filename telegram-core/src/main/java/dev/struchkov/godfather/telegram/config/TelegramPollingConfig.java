package dev.struchkov.godfather.telegram.config;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class TelegramPollingConfig {

    private String botUsername;
    private String botToken;

    private ProxyConfig proxyConfig;

    public TelegramPollingConfig(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public TelegramPollingConfig() {
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public void setBotToken(String botToken) {
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
