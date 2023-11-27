package dev.struchkov.godfather.telegram.main.context.convert;

import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.main.domain.content.EditedMail;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ContactAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.attachment.PictureGroupAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.StickerAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.VideoAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.VoiceAttachment;
import dev.struchkov.godfather.telegram.main.context.MailPayload;
import dev.struchkov.haiti.utils.Checker;
import dev.struchkov.haiti.utils.Strings;
import org.glassfish.grizzly.http.util.MimeType;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [18.08.2019]
 */
public final class MessageMailConvert {

    private MessageMailConvert() {
        utilityClass();
    }

    public static EditedMail applyEdited(Message message) {
        return EditedMail.builder()
                .editDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getEditDate()), ZoneId.systemDefault()))
                .newMail(apply(message))
                .build();
    }

    public static Mail apply(Message message) {
        final Mail mail = new Mail();

        final Long chatId = message.getChatId();
        mail.setId(message.getMessageId().toString());
        mail.setFromPersonId(chatId != null ? chatId.toString() : null);
        mail.setText(getText(message));
        mail.setCreateDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()));

        final Chat chat = message.getChat();
        mail.setFirstName(chat.getFirstName());
        mail.setLastName(chat.getLastName());
        mail.addPayload(MailPayload.USERNAME, chat.getUserName());

        convertDocument(message.getDocument()).ifPresent(mail::addAttachment);
        convertContact(message.getContact()).ifPresent(mail::addAttachment);
        convertPhoto(message.getPhoto()).ifPresent(mail::addAttachment);
        convertVideo(message.getVideo()).ifPresent(mail::addAttachment);
        convertVoice(message.getVoice()).ifPresent(mail::addAttachment);
        convertSticker(message.getSticker()).ifPresent(mail::addAttachment);

        final List<MessageEntity> entities = message.getEntities();
        if (entities != null) {
            mail.addAttachments(convertAttachments(message));
        }

        if (message.getReplyToMessage() != null) {
            mail.setReplayMail(apply(message.getReplyToMessage()));
        }

        return mail;
    }

    private static String getText(Message message) {
        if (checkNotBlank(message.getText())) {
            return message.getText();
        }
        if (checkNotBlank(message.getCaption())) {
            return message.getCaption();
        }
        return null;
    }

    private static Optional<Attachment> convertPhoto(List<PhotoSize> photoSizes) {
        if (photoSizes != null && !photoSizes.isEmpty()) {
            final PictureGroupAttachment attachment = new PictureGroupAttachment();

            final List<Picture> pictures = photoSizes.stream()
                    .map(photoSize -> {
                        final Picture picture = new Picture();
                        picture.setFileSize(photoSize.getFileSize().longValue());
                        picture.setFileId(photoSize.getFileId());
                        picture.setHeight(photoSize.getHeight());
                        picture.setWeight(photoSize.getWidth());
                        picture.setMimeType("image/jpeg");
                        picture.setFileName(generateFileName("image/jpeg"));
                        return picture;
                    }).toList();

            attachment.setPictures(pictures);

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

    private static Optional<VoiceAttachment> convertVoice(Voice voice) {
        if (voice != null) {
            final VoiceAttachment attachment = new VoiceAttachment();
            attachment.setFileId(voice.getFileId());
            attachment.setFileSize(voice.getFileSize());
            attachment.setMimeType(voice.getMimeType());
            attachment.setDuration(Duration.ofSeconds(voice.getDuration()));
            attachment.setFileName(generateFileName(voice.getMimeType()));
            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static Optional<StickerAttachment> convertSticker(Sticker sticker) {
        if (sticker != null) {
            final StickerAttachment attachment = new StickerAttachment();
            attachment.setFileId(sticker.getFileId());
            attachment.setFileSize(sticker.getFileSize().longValue());
            attachment.setAnimated(sticker.getIsAnimated());
            attachment.setVideo(sticker.getIsVideo());
            attachment.setFileName(UUID.randomUUID().toString());
            attachment.setEmoji(sticker.getEmoji());
            return Optional.of(attachment);
        }
        return Optional.empty();
    }

    private static String generateFileName(String mimeType) {
        final StringBuilder builder = new StringBuilder(UUID.randomUUID().toString());
        switch (mimeType) {
            case "audio/ogg" -> builder.append(".ogg");
            case "image/png" -> builder.append(".png");
            case "image/jpeg" -> builder.append(".jpg");
            default -> {}
        }
        return builder.toString();
    }

    private static Optional<VideoAttachment> convertVideo(Video video) {
        if (video != null) {
            final VideoAttachment attachment = new VideoAttachment();
            attachment.setFileId(video.getFileId());
            attachment.setFileSize(video.getFileSize());
            attachment.setFileName(video.getFileName());
            attachment.setMimeType(video.getMimeType());
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
