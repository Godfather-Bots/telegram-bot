package dev.struchkov.godfather.telegram.simple.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.domain.config.WebhookConfig;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TelegramWebhookConnect implements TelegramConnect {

    private TelegramWebhookBot webhookBot;

    public TelegramWebhookConnect(TelegramBotConfig telegramBotConfig) {
        initWebHook(telegramBotConfig);
    }

    private void initWebHook(TelegramBotConfig telegramBotConfig) {
        try {
            final TelegramWebhookBot bot = new TelegramWebhookBot(telegramBotConfig);
            final WebhookConfig webhookConfig = telegramBotConfig.getWebhookConfig();
            if (webhookConfig.isEnable()) {
                log.info("Инициализация webhook соединения. {}", telegramBotConfig.getWebhookConfig());
                final SetWebhook setWebhook = SetWebhook.builder()
                        .secretToken(webhookConfig.getSecretToken())
                        .url(webhookConfig.getUrl())
                        .build();
                bot.setWebhook(setWebhook);
                webhookBot = bot;
                log.info("Инициализация webhook соединения прошла успешно.");
            } else {
                log.debug("Webhook соединение не устанавливалось.");
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public AbsSender getAbsSender() {
        return webhookBot.getAdsSender();
    }

    @Override
    public String getToken() {
        return webhookBot.getBotToken();
    }

    @Override
    public void initEventDistributor(EventDistributor eventDistributorService) {
        webhookBot.initEventDistributor(eventDistributorService);
    }

}
