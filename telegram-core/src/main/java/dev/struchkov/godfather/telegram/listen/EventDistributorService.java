package dev.struchkov.godfather.telegram.listen;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.service.EventProvider;
import dev.struchkov.godfather.telegram.convert.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.convert.MessageMailConvert;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorService implements EventDistributor {

    private final List<EventProvider<Mail>> eventProviders;

    public EventDistributorService(TelegramConnect telegramConnect, List<EventProvider<Mail>> eventProviders) {
        this.eventProviders = eventProviders;
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public void processing(@NotNull Update update) {
        final Message message = update.getMessage();
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        if (message != null) {
            eventProviders.forEach(provider -> provider.sendEvent(MessageMailConvert.apply(message)));
        }
        if (callbackQuery != null) {
            eventProviders.forEach(provider -> provider.sendEvent(CallbackQueryConvert.apply(callbackQuery)));
        }
    }

}
