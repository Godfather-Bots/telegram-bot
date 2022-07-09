package dev.struchkov.godfather.telegram.utils;

import dev.struchkov.godfather.context.domain.content.attachment.Attachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.TelegramAttachmentType;
import dev.struchkov.haiti.utils.Inspector;

import java.util.Collection;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class Attachments {

    private Attachments() {
        utilityClass();
    }

    public static Optional<DocumentAttachment> findFirstDocument(Collection<Attachment> attachments) {
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (isDocument(attachment)) {
                    return Optional.ofNullable((DocumentAttachment) attachment);
                }
            }
        }
        return Optional.empty();
    }

    public static boolean hasDocument(Collection<Attachment> attachments) {
        Inspector.isNotNull(attachments);
        for (Attachment attachment : attachments) {
            if (isDocument(attachment)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDocument(Attachment attachment) {
        Inspector.isNotNull(attachment);
        return TelegramAttachmentType.DOCUMENT.name().equals(attachment.getType());
    }

}
