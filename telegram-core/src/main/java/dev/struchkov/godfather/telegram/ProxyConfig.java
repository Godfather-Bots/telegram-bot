package dev.struchkov.godfather.telegram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfig {

    private String host;
    private Integer port;
    private String user;
    private String password;
    private Type type;

    public enum Type {
        SOCKS5, SOCKS4, HTTP
    }

}
