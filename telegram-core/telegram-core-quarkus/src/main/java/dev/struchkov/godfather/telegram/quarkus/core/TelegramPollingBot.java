package dev.struchkov.godfather.telegram.quarkus.core;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramBot;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Field;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [15/07/2019]
 */
public class TelegramPollingBot extends TelegramLongPollingBot implements TelegramBot {

    private final Vertx vertx;
    private final TelegramBotConfig telegramBotConfig;
    private EventDistributor eventDistributor;

    @SneakyThrows
    public TelegramPollingBot(Vertx vertx, TelegramBotConfig telegramBotConfig, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.telegramBotConfig = telegramBotConfig;
        this.vertx = vertx;
        final Field field = this.getClass().getSuperclass().getSuperclass().getDeclaredField("exe");
        // Делаем поле exe доступным для изменений
        field.setAccessible(true);
        // Заменяем поле exe в экземпляре наследника
        field.set(this, Infrastructure.getDefaultExecutor());
        // Закрываем доступ к полю exe
        field.setAccessible(false);
    }

    public TelegramPollingBot(Vertx vertx, TelegramBotConfig telegramBotConfig) {
        this.vertx = vertx;
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        vertx.runOnContext(v -> handleUpdate(update));
    }

    private void handleUpdate(Update update) {
        if (update != null && eventDistributor != null) {
            eventDistributor.processing(update)
                    .subscribe().asCompletionStage();
        }
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
    public AbsSender getAdsSender() {
        return this;
    }

    @Override
    public void initEventDistributor(@NotNull EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

}
