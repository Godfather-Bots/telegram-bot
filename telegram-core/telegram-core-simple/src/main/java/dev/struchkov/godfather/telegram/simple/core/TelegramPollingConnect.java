package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.ProxyConfig.Type;
import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramBot;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class TelegramPollingConnect implements TelegramConnect {

    private static final Logger log = LoggerFactory.getLogger(TelegramPollingConnect.class);

    private TelegramBot telegramBot;
    private final TelegramBotConfig telegramBotConfig;

    public TelegramPollingConnect(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
        initLongPolling(telegramBotConfig);
    }

    private void initLongPolling(TelegramBotConfig telegramBotConfig) {
        final ProxyConfig proxyConfig = telegramBotConfig.getProxyConfig();
        if (checkNotNull(proxyConfig) && checkNotNull(proxyConfig.getPassword()) && !"".equals(proxyConfig.getPassword())) {
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
            if (checkNotNull(proxyConfig) && proxyConfig.isEnable() && checkNotNull(proxyConfig.getHost()) && !"".equals(proxyConfig.getHost())) {
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//                System.setProperty("javax.net.debug", "all");
                log.info(System.getProperty("https.protocols"));
                DefaultBotOptions botOptions = new DefaultBotOptions();
                botOptions.setProxyHost(proxyConfig.getHost());
                botOptions.setProxyPort(proxyConfig.getPort());
                botOptions.setProxyType(convertProxyType(proxyConfig.getType()));

                final TelegramPollingBot bot = new TelegramPollingBot(telegramBotConfig, botOptions);

                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            } else {
                final TelegramPollingBot bot = new TelegramPollingBot(telegramBotConfig);
                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private DefaultBotOptions.ProxyType convertProxyType(Type type) {
        return switch (type) {
            case SOCKS5 -> DefaultBotOptions.ProxyType.SOCKS5;
            case SOCKS4 -> DefaultBotOptions.ProxyType.SOCKS4;
            case HTTP -> DefaultBotOptions.ProxyType.HTTP;
        };
    }

    public void initEventDistributor(EventDistributor eventDistributor) {
        telegramBot.initEventDistributor(eventDistributor);
    }

    public String getToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public AbsSender getAbsSender() {
        return telegramBot.getAdsSender();
    }

}