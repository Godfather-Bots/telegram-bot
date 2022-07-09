package dev.struchkov.godfather.telegram.convert;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.domain.content.attachment.Attachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public final class MessageMailConvert {

    public MessageMailConvert() {
        utilityClass();
    }

    public static Mail apply(Message message) {
        final Mail mail = new Mail();
        mail.setPersonId(message.getChatId());
        mail.setAddDate(LocalDateTime.now());
        mail.setText(message.getText());
        mail.setCreateDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()));
        mail.setFirstName(message.getChat().getFirstName());
        mail.setLastName(message.getChat().getLastName());

        convertDocument(message.getDocument()).ifPresent(mail::addAttachment);

        final List<MessageEntity> entities = message.getEntities();
        if (entities != null) {
            mail.addAttachments(convertAttachments(entities));
        }

        if (message.getReplyToMessage() != null) {
            mail.setForwardMail(Collections.singletonList(apply(message.getReplyToMessage())));
        }

        return mail;
    }

    private static Optional<DocumentAttachment> convertDocument(Document document) {
        if (document != null) {
            final DocumentAttachment attachment = new DocumentAttachment();
            attachment.setFileId(document.getFileId());
            attachment.setFileSize(document.getFileSize());
            attachment.setFileName(document.getFileName());
            attachment.setFileType(document.getMimeType());
            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static List<Attachment> convertAttachments(List<MessageEntity> entities) {
        final List<Attachment> attachments = new ArrayList<>();
//        for (MessageEntity entity : entities) {
//            String type = entity.getType();
//            if ("text_link".equals(type)) {
//                Link link = new Link();
//                link.setUrl(entity.getUrl());
//                attachments.add(link);
//            }
//        }
        return attachments;
    }

}
