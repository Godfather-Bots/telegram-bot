package dev.struchkov.godfather.telegram.main.consumer;

import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.main.domain.content.ChatMail;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ContactAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.attachment.PictureGroupAttachment;
import dev.struchkov.godfather.telegram.main.context.MailPayload;
import dev.struchkov.haiti.utils.Checker;
import dev.struchkov.haiti.utils.Strings;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public final class MessageChatMailConvert {

    private MessageChatMailConvert() {
        utilityClass();
    }

    public static ChatMail apply(Message message) {
        final ChatMail mail = new ChatMail();

        final Long chatId = message.getChatId();
        mail.setId(message.getMessageId().toString());
        mail.setChatId(chatId.toString());
        mail.setText(message.getText());
        mail.setCreateDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()));

        final User fromUser = message.getFrom();
        mail.setFirstName(fromUser.getFirstName());
        mail.setLastName(fromUser.getLastName());
        mail.setPayload(MailPayload.USERNAME, fromUser.getUserName());
        mail.setFromPersonId(fromUser.getId().toString());

        convertDocument(message.getDocument()).ifPresent(mail::addAttachment);
        convertContact(message.getContact()).ifPresent(mail::addAttachment);
        convertPhoto(message.getPhoto()).ifPresent(mail::addAttachment);

        final List<MessageEntity> entities = message.getEntities();
        if (entities != null) {
            mail.addAttachments(convertAttachments(message));
        }

        if (message.getReplyToMessage() != null) {
            mail.setForwardMail(Collections.singletonList(apply(message.getReplyToMessage())));
        }

        return mail;
    }

    private static Optional<Attachment> convertPhoto(List<PhotoSize> photoSizes) {
        if (photoSizes != null && !photoSizes.isEmpty()) {
            final PictureGroupAttachment attachment = new PictureGroupAttachment();

            final List<Picture> pictures = photoSizes.stream()
                    .map(photoSize -> {
                        final Picture picture = new Picture();
                        picture.setFileSize(photoSize.getFileSize());
                        picture.setFileId(photoSize.getFileId());
                        picture.setHeight(photoSize.getHeight());
                        picture.setWeight(photoSize.getWidth());
                        picture.setFileUniqueId(photoSize.getFileUniqueId());
                        return picture;
                    }).toList();

            attachment.setPictureSizes(pictures);

            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static Optional<ContactAttachment> convertContact(Contact contact) {
        if (contact != null) {
            final ContactAttachment attachment = new ContactAttachment();
            attachment.setPhoneNumber(contact.getPhoneNumber());
            attachment.setUserId(contact.getUserId());
            attachment.setFirstName(contact.getFirstName());
            attachment.setLastName(contact.getLastName());
            if (contact.getVCard() != null) {
                attachment.setOwner(false);
                attachment.setVCard(contact.getVCard());
            } else {
                attachment.setOwner(true);
            }
            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static Optional<DocumentAttachment> convertDocument(Document document) {
        if (document != null) {
            final DocumentAttachment attachment = new DocumentAttachment();
            attachment.setFileId(document.getFileId());
            attachment.setFileSize(document.getFileSize());
            attachment.setFileName(document.getFileName());
            attachment.setMimeType(document.getMimeType());
            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static List<Attachment> convertAttachments(Message message) {
        final List<MessageEntity> entities = message.getEntities();
        if (Checker.checkNotEmpty(entities)) {
            return entities.stream()
                    .map(entity -> convertEntity(message, entity))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }
        return Collections.emptyList();
    }

    private static Optional<Attachment> convertEntity(Message message, MessageEntity entity) {
        switch (entity.getType()) {
            case "text_link" -> {
                return Optional.of(entity.getUrl())
                        .map(LinkAttachment::new);
            }
            case "url" -> {
                return Optional.of(entity.getText())
                        .map(LinkAttachment::new);
            }
            case "bot_command" -> {
                final String commandValue = entity.getText();
                String commandArg = message.getText().replace(commandValue, "");
                if (Checker.checkNotEmpty(commandArg)) {
                    commandArg = commandArg.substring(1);
                }
                final CommandAttachment commandAttachment = new CommandAttachment();
                commandAttachment.setValue(commandValue);
                commandAttachment.setCommandType(commandValue.replace("/", ""));
                commandAttachment.setArg(Strings.EMPTY.equals(commandArg) ? null : commandArg);
                commandAttachment.setRawValue(message.getText());
                return Optional.of(commandAttachment);
            }
        }
        return Optional.empty();
    }

}
