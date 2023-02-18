package dev.struchkov.godfather.telegram.domain.config;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class TelegramBotConfig {

    private String username;
    private String token;

    private ProxyConfig proxyConfig;

    public TelegramBotConfig(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public TelegramBotConfig() {
    }

    public void setUsername(String botUsername) {
        this.username = botUsername;
    }

    public void setToken(String botToken) {
        this.token = botToken;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

}
