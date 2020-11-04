package org.sadtech.telegram.bot.listen;

import lombok.NonNull;
import org.sadtech.social.core.service.MailService;
import org.sadtech.telegram.bot.convert.CallbackQueryConvert;
import org.sadtech.telegram.bot.convert.MessageMailConvert;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorImpl implements EventDistributor {

    private final MailService mailService;

    public EventDistributorImpl(TelegramConnect telegramConnect, MailService mailService) {
        this.mailService = mailService;
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public void processing(@NonNull Update update) {
        final Message message = update.getMessage();
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        if (message != null) {
            mailService.add(MessageMailConvert.apply(message));
        }
        if (callbackQuery != null) {
            mailService.add(CallbackQueryConvert.apply(callbackQuery));
        }
    }

}
