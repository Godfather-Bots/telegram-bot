package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.ProxyConfig.Type;
import dev.struchkov.godfather.telegram.domain.config.TelegramConnectConfig;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class TelegramConnectBot implements TelegramConnect {

    private static final Logger log = LoggerFactory.getLogger(TelegramConnectBot.class);

    private TelegramBot telegramBot;
    private final TelegramConnectConfig telegramConnectConfig;

    public TelegramConnectBot(TelegramConnectConfig telegramConnectConfig) {
        this.telegramConnectConfig = telegramConnectConfig;
        initLongPolling(telegramConnectConfig);
    }

    private void initLongPolling(TelegramConnectConfig telegramConnectConfig) {

        final ProxyConfig proxyConfig = telegramConnectConfig.getProxyConfig();
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

        final TelegramBotsApi botapi;
        try {
            if (proxyConfig != null && proxyConfig.getHost() != null) {
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
                System.setProperty("javax.net.debug", "all");
                log.info(System.getProperty("https.protocols"));
                DefaultBotOptions botOptions = new DefaultBotOptions();
                botOptions.setProxyHost(proxyConfig.getHost());
                botOptions.setProxyPort(proxyConfig.getPort());
                botOptions.setProxyType(convertProxyType(proxyConfig.getType()));

                final TelegramPollingBot bot = new TelegramPollingBot(telegramConnectConfig, botOptions);

                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            } else {
                final TelegramPollingBot bot = new TelegramPollingBot(telegramConnectConfig);
                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private DefaultBotOptions.ProxyType convertProxyType(Type type) {
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

    public void initEventDistributor(EventDistributor eventDistributor) {
        telegramBot.initEventDistributor(eventDistributor);
    }

    public String getToken() {
        return telegramConnectConfig.getBotToken();
    }

    @Override
    public AbsSender getAbsSender() {
        return telegramBot.getAdsSender();
    }

}