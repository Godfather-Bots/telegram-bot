package dev.struchkov.godfather.telegram.quarkus.core;

import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.ProxyConfig.Type;
import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TelegramPollingConnect implements TelegramConnect {

    private TelegramPollingBot pollingBot;

    public TelegramPollingConnect(TelegramBotConfig telegramBotConfig) {
        initLongPolling(telegramBotConfig);
    }

    private void initLongPolling(TelegramBotConfig telegramBotConfig) {
        final ProxyConfig proxyConfig = telegramBotConfig.getProxyConfig();
        if (checkNotNull(proxyConfig) && proxyConfig.isEnable() && checkNotNull(proxyConfig.getPassword()) && !"".equals(proxyConfig.getPassword())) {
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
                this.pollingBot = bot;
            } else {
                final TelegramPollingBot bot = new TelegramPollingBot(telegramBotConfig);
                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.pollingBot = bot;
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

    @Override
    public String getToken() {
        return pollingBot.getBotToken();
    }

    @Override
    public void initEventDistributor(EventDistributor eventDistributorService) {
        pollingBot.initEventDistributor(eventDistributorService);
    }

    @Override
    public AbsSender getAbsSender() {
        return pollingBot.getAdsSender();
    }

}