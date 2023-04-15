package dev.struchkov.godfather.telegram.quarkus.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramBot;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Field;

@Slf4j
public class TelegramWebhookBot extends org.telegram.telegrambots.bots.TelegramWebhookBot implements TelegramBot {

    private final TelegramBotConfig telegramBotConfig;
    private EventDistributor eventDistributor;

    @SneakyThrows
    public TelegramWebhookBot(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
        final Field field = this.getClass().getSuperclass().getSuperclass().getDeclaredField("exe");
        // Делаем поле exe доступным для изменений
        field.setAccessible(true);
        // Заменяем поле exe в экземпляре наследника
        field.set(this, Infrastructure.getDefaultExecutor());
        // Закрываем доступ к полю exe
        field.setAccessible(false);
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        eventDistributor.processing(update).subscribe().with(v -> {
        });
        return null;
    }

    @Override
    public String getBotPath() {
        return "bot";
    }

    @Override
    public AbsSender getAdsSender() {
        return this;
    }

    @Override
    public void initEventDistributor(EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
