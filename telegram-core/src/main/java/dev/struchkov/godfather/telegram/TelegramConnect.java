package dev.struchkov.godfather.telegram;

import dev.struchkov.godfather.telegram.config.ProxyConfig;
import dev.struchkov.godfather.telegram.config.TelegramPollingConfig;
import dev.struchkov.godfather.telegram.listen.EventDistributorService;
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
public class TelegramConnect {

    private static final Logger log = LoggerFactory.getLogger(TelegramConnect.class);

    private TelegramBot telegramBot;
    private final TelegramPollingConfig telegramPollingConfig;

    public TelegramConnect(TelegramPollingConfig telegramPollingConfig) {
        this.telegramPollingConfig = telegramPollingConfig;
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


                final TelegramPollingBot bot = new TelegramPollingBot(telegramPollingConfig, botOptions);


                botapi = new TelegramBotsApi(DefaultBotSession.class);
                botapi.registerBot(bot);
                this.telegramBot = bot;
            } else {
                final TelegramPollingBot bot = new TelegramPollingBot(telegramPollingConfig);
                botapi = new TelegramBotsApi(DefaultBotSession.class);
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

    public AbsSender getAdsSender() {
        return telegramBot.getAdsSender();
    }

    public void initEventDistributor(EventDistributorService eventDistributor) {
        telegramBot.initEventDistributor(eventDistributor);
    }

    public String getToken() {
        return telegramPollingConfig.getBotToken();
    }

}
