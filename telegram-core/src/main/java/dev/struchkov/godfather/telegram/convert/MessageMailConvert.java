package dev.struchkov.godfather.telegram.convert;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.domain.content.attachment.Attachment;
import dev.struchkov.godfather.context.domain.content.attachment.Link;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        mail.setFirstName(message.getChat().getFirstName());
        mail.setLastName(message.getChat().getLastName());
        List<MessageEntity> entities = message.getEntities();
        if (entities != null) {
            mail.setAttachments(convertAttachments(entities));
        }

        if (message.getReplyToMessage() != null) {
            mail.setForwardMail(Collections.singletonList(apply(message.getReplyToMessage())));
        }

        return mail;
    }

    private static List<Attachment> convertAttachments(List<MessageEntity> entities) {
        final List<Attachment> attachments = new ArrayList();
        for (MessageEntity entity : entities) {
            String type = entity.getType();
            if ("text_link".equals(type)) {
                Link link = new Link();
                link.setUrl(entity.getUrl());
                attachments.add(link);
            }
        }
        return attachments;
    }

}
