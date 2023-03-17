package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

public class TelegramDefaultConnect implements TelegramConnect {

    private final String botToken;
    private final AbsSender absSender;

    public TelegramDefaultConnect(TelegramBotConfig connectConfig) {
        this.botToken = connectConfig.getToken();
        this.absSender = createAbsSender(connectConfig);
    }

    @NotNull
    private DefaultAbsSender createAbsSender(TelegramBotConfig connectConfig) {
        final DefaultBotOptions botOptions = new DefaultBotOptions();

        final ProxyConfig proxyConfig = connectConfig.getProxyConfig();
        if (checkNotNull(proxyConfig) && proxyConfig.isEnable()) {
            if (checkNotNull(proxyConfig.getPassword())) {
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

            if (checkNotNull(proxyConfig.getHost())) {
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//                System.setProperty("javax.net.debug", "all");
                botOptions.setProxyHost(proxyConfig.getHost());
                botOptions.setProxyPort(proxyConfig.getPort());
                botOptions.setProxyType(convertProxyType(proxyConfig.getType()));
            }
        }

        return new DefaultAbsSender(botOptions) {
            @Override
            public String getBotToken() {
                return botToken;
            }
        };
    }

    private DefaultBotOptions.ProxyType convertProxyType(ProxyConfig.Type type) {
        return switch (type) {
            case SOCKS5 -> DefaultBotOptions.ProxyType.SOCKS5;
            case SOCKS4 -> DefaultBotOptions.ProxyType.SOCKS4;
            case HTTP -> DefaultBotOptions.ProxyType.HTTP;
        };
    }

    @Override
    public AbsSender getAbsSender() {
        return absSender;
    }

    @Override
    public String getToken() {
        return botToken;
    }

    @Override
    public void initEventDistributor(EventDistributor eventDistributorService) {
        throw new IllegalStateException();
    }

}
