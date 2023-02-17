package dev.struchkov.godfather.telegram.domain.config;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class ProxyConfig {

    private boolean enable = true;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private Type type;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public enum Type {
        SOCKS5, SOCKS4, HTTP
    }

}
