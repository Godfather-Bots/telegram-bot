package dev.struchkov.godfather.telegram.domain.config;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public class TelegramBotConfig {

    private String name;
    private String token;

    private ProxyConfig proxyConfig;

    public TelegramBotConfig(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public TelegramBotConfig() {
    }

    public void setBotUsername(String botUsername) {
        this.name = botUsername;
    }

    public void setBotToken(String botToken) {
        this.token = botToken;
    }

    public String getName() {
        return name;
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
