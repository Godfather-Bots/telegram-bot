package org.sadtech.telegram.bot.listen;

import lombok.extern.slf4j.Slf4j;
import org.sadtech.telegram.bot.ProxyConfig;
import org.sadtech.telegram.bot.TelegramBot;
import org.sadtech.telegram.bot.TelegramPollingBot;
import org.sadtech.telegram.bot.config.TelegramPollingConfig;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Slf4j
public class TelegramConnect {

    static {
        ApiContextInitializer.init();
    }

    private TelegramBot telegramBot;

    public TelegramConnect(TelegramPollingConfig telegramPollingConfig) {
        initLongPolling(telegramPollingConfig);
    }

//    public TelegramConnect(TelegramWebHookConfig telegramWebHookConfig) {
//        initWebHook(telegramWebHookConfig);
//    }
//
//    private void initWebHook(TelegramWebHookConfig telegramWebHookConfig) {
//        TelegramBotsApi botapi = new TelegramBotsApi();
//        final TelegramWebhookBot telegramWebhookBot = new TelegramHookBot(telegramWebHookConfig);
//        try {
//            botapi.registerBot(telegramWebhookBot);
//            this.telegramBot = (TelegramBot) telegramWebhookBot;
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        }
//    }

    private void initLongPolling(TelegramPollingConfig telegramPollingConfig) {
        final ProxyConfig proxyConfig = telegramPollingConfig.getProxyConfig();
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

        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            if (proxyConfig != null && proxyConfig.getHost() != null) {
                System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
                System.setProperty("javax.net.debug", "all");
                log.info(System.getProperty("https.protocols"));
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
                botOptions.setProxyHost(proxyConfig.getHost());
                botOptions.setProxyPort(proxyConfig.getPort());
                botOptions.setProxyType(convertProxyType(proxyConfig.getType()));
                final TelegramPollingBot bot = new TelegramPollingBot(telegramPollingConfig, botOptions);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            } else {
                final TelegramPollingBot bot = new TelegramPollingBot(telegramPollingConfig);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
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

    AbsSender getAdsSender() {
        return telegramBot.getAdsSender();
    }

    void initEventDistributor(EventDistributorImpl eventDistributor) {
        telegramBot.initEventDistributor(eventDistributor);
    }

}
