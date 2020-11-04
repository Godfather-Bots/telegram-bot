package org.sadtech.telegram.bot.convert;

import org.sadtech.social.core.domain.content.Mail;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDateTime;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
public class CallbackQueryConvert {

    public static Mail apply(CallbackQuery callbackQuery) {
        final Mail mail = new Mail();
        mail.setText(callbackQuery.getData());
        mail.setPersonId(callbackQuery.getMessage().getChatId());
        mail.setAddDate(LocalDateTime.now());
        return mail;
    }

}
