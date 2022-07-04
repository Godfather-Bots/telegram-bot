package dev.struchkov.godfather.telegram.convert;

import dev.struchkov.godfather.context.domain.content.Mail;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

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

        final User user = callbackQuery.getFrom();
        mail.setFirstName(user.getFirstName());
        mail.setLastName(user.getLastName());
        return mail;
    }

}
