package dev.struchkov.godfather.telegram.main.core.util;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;

import java.util.Arrays;
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

    public static Predicate<Mail> isCommandByType(String commandType) {
        return mail -> {
            final Optional<CommandAttachment> optCommand = Attachments.findFirstCommand(mail.getAttachments());
            if (optCommand.isPresent()) {
                final CommandAttachment command = optCommand.get();
                final String type = command.getCommandType();
                return type.equals(commandType);
            }
            return false;
        };
    }

    public static Predicate<Mail> isButtonClick() {
        return mail -> Attachments.findFirstButtonClick(mail.getAttachments()).isPresent();
    }

    public static Predicate<Mail> isLinks() {
        return mail -> Attachments.findFirstLink(mail.getAttachments()).isPresent();
    }

    public static Predicate<Mail> isButtonClickArg(String argType) {
        return mail -> Attachments.findFirstButtonClick(mail.getAttachments())
                .flatMap(click -> click.getArgByType(argType))
                .isPresent();
    }

    public static Predicate<Mail> isButtonClickArgValue(String argType, String argValue) {
        return mail -> Attachments.findFirstButtonClick(mail.getAttachments())
                .flatMap(click -> click.getArgByType(argType))
                .filter(buttonArg -> argValue.equals(buttonArg.getValue()))
                .isPresent();
    }

    public static Predicate<Mail> isPersonId(String... personId) {
        return mail -> Arrays.stream(personId).anyMatch(id -> id.equals(mail.getFromPersonId()));
    }

}
