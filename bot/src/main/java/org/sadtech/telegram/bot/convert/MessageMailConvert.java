package org.sadtech.telegram.bot.convert;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sadtech.social.core.domain.content.Mail;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageMailConvert {

    public static Mail apply(Message message) {
        Mail mail = new Mail();
        mail.setPersonId(message.getChatId());
        mail.setAddDate(LocalDateTime.now());
        mail.setText(message.getText());
        mail.setCreateDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()));
        return mail;
    }

}
