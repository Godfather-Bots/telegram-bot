package dev.struchkov.godfather.telegram.simple.core.util;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;

import java.util.Optional;
import java.util.function.Predicate;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public class UnitTrigger {

    private UnitTrigger() {
        utilityClass();
    }

    public static Predicate<Mail> clickButtonRaw(String rawCallBackData) {
        return mail -> {
            final Optional<ButtonClickAttachment> optButtonClick = Attachments.findFirstButtonClick(mail.getAttachments());
            if (optButtonClick.isPresent()) {
                final ButtonClickAttachment buttonClick = optButtonClick.get();
                final String rawData = buttonClick.getRawCallBackData();
                return rawData.equals(rawCallBackData);
            }
            return false;
        };
    }

    public static Predicate<Mail> isClickButton() {
        return mail -> Attachments.findFirstButtonClick(mail.getAttachments()).isPresent();
    }

    public static Predicate<Mail> isLinks() {
        return mail -> Attachments.findFirstLink(mail.getAttachments()).isPresent();
    }

}
