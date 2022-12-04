package dev.struchkov.godfather.telegram.main.core.util;

import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ContactAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.attachment.PictureGroupAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.TelegramAttachmentType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public final class Attachments {

    private Attachments() {
        utilityClass();
    }

    public static List<LinkAttachment> findAllLinks(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            return attachments.stream()
                    .filter(Attachments::isLink)
                    .map(LinkAttachment.class::cast)
                    .toList();
        }
        return Collections.emptyList();
    }

    public static Optional<LinkAttachment> findFirstLink(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isLink(attachment)) {
                    return Optional.of((LinkAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<ButtonClickAttachment> findFirstButtonClick(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isButtonClick(attachment)) {
                    return Optional.of((ButtonClickAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<PictureGroupAttachment> findFirstPictureGroup(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isPictureGroup(attachment)) {
                    return Optional.of((PictureGroupAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Picture> findFirstLargePicture(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isPictureGroup(attachment)) {
                    final PictureGroupAttachment pictureGroup = (PictureGroupAttachment) attachment;
                    return pictureGroup.getLargePicture();
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<DocumentAttachment> findFirstDocument(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isDocument(attachment)) {
                    return Optional.of((DocumentAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<ContactAttachment> findFirstContact(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isContact(attachment)) {
                    return Optional.of((ContactAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<CommandAttachment> findFirstCommand(Collection<Attachment> attachments) {
        if (checkNotEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                if (isCommand(attachment)) {
                    return Optional.of((CommandAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static boolean hasDocument(Collection<Attachment> attachments) {
        isNotNull(attachments);
        for (Attachment attachment : attachments) {
            if (isDocument(attachment)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCommand(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.COMMAND.name().equals(attachment.getType());
    }

    public static boolean isDocument(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.DOCUMENT.name().equals(attachment.getType());
    }

    private static boolean isContact(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.CONTACT.name().equals(attachment.getType());
    }

    private static boolean isPictureGroup(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.PICTURE.name().equals(attachment.getType());
    }

    private static boolean isLink(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.LINK.name().equals(attachment.getType());
    }

    public static boolean isButtonClick(Attachment attachment) {
        isNotNull(attachment);
        return TelegramAttachmentType.BUTTON_CLICK.name().equals(attachment.getType());
    }

}
