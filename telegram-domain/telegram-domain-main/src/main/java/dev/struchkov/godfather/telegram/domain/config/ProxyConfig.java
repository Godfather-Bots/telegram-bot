package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Getter
@Setter
public class ProxyConfig {

    private boolean enable = false;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private Type type;

    public enum Type {
        SOCKS5, SOCKS4, HTTP
    }

}
