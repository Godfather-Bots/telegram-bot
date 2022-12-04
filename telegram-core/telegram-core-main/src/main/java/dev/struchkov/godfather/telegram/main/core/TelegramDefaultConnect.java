package dev.struchkov.godfather.telegram.main.core;

import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.TelegramConnectConfig;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class TelegramDefaultConnect implements TelegramConnect {

    private final String botToken;
    private final AbsSender absSender;

    public TelegramDefaultConnect(TelegramConnectConfig connectConfig) {
        this.botToken = connectConfig.getBotToken();
        this.absSender = createAbsSender(connectConfig);
    }

    @NotNull
    private DefaultAbsSender createAbsSender(TelegramConnectConfig connectConfig) {
        final DefaultBotOptions botOptions = new DefaultBotOptions();

        final ProxyConfig proxyConfig = connectConfig.getProxyConfig();
        if (proxyConfig != null && proxyConfig.getPassword() != null) {
            try {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                proxyConfig.getUser(),
                                proxyConfig.getPassword().toCharArray()
                        );
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (proxyConfig != null && proxyConfig.getHost() != null) {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            System.setProperty("javax.net.debug", "all");
            botOptions.setProxyHost(proxyConfig.getHost());
            botOptions.setProxyPort(proxyConfig.getPort());
            botOptions.setProxyType(convertProxyType(proxyConfig.getType()));
        }

        return new DefaultAbsSender(botOptions) {
            @Override
            public String getBotToken() {
                return botToken;
            }
        };
    }

    private DefaultBotOptions.ProxyType convertProxyType(ProxyConfig.Type type) {
        switch (type) {
            case SOCKS5:
                return DefaultBotOptions.ProxyType.SOCKS5;
            case SOCKS4:
                return DefaultBotOptions.ProxyType.SOCKS4;
            case HTTP:
                return DefaultBotOptions.ProxyType.HTTP;
            default:
                return DefaultBotOptions.ProxyType.NO_PROXY;
        }
    }

    @Override
    public AbsSender getAbsSender() {
        return absSender;
    }

    @Override
    public String getToken() {
        return botToken;
    }

}
