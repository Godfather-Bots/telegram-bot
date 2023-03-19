package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Getter
@Setter
@ToString
public class ProxyConfig {

    private boolean enable = false;

    @ToString.Exclude
    private String host;

    @ToString.Exclude
    private Integer port;

    @ToString.Exclude
    private String user;

    @ToString.Exclude
    private String password;

    @ToString.Exclude
    private Type type;

    public enum Type {
        SOCKS5, SOCKS4, HTTP
    }

}
