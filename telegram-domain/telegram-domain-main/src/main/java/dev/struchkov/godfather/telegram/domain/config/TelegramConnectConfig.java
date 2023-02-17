package dev.struchkov.godfather.telegram.domain.config;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class TelegramConnectConfig {

    private String botUsername;
    private String botToken;

    private ProxyConfig proxyConfig;

    public TelegramConnectConfig(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public TelegramConnectConfig() {
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
