package dev.struchkov.godfather.telegram.quarkus.core.util;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.quarkus.domain.unit.func.UniPredicate;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;

import java.util.Optional;

import static dev.struchkov.godfather.quarkus.domain.unit.func.UniPredicate.predicate;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public class UnitTrigger {

    private UnitTrigger() {
        utilityClass();
    }

    public static UniPredicate<Mail> clickButtonRaw(String rawCallBackData) {
        return predicate(
                mail -> {
                    final Optional<ButtonClickAttachment> optButtonClick = Attachments.findFirstButtonClick(mail.getAttachments());
                    if (optButtonClick.isPresent()) {
                        final ButtonClickAttachment buttonClick = optButtonClick.get();
                        final String rawData = buttonClick.getRawCallBackData();
                        return rawData.equals(rawCallBackData);
                    }
                    return false;
                }
        );
    }

    public static UniPredicate<Mail> isCommandByType(String commandType) {
        return predicate(
                mail -> {
                    final Optional<CommandAttachment> optCommand = Attachments.findFirstCommand(mail.getAttachments());
                    if (optCommand.isPresent()) {
                        final CommandAttachment command = optCommand.get();
                        final String type = command.getCommandType();
                        return type.equals(commandType);
                    }
                    return false;
                }
        );
    }

    public static UniPredicate<Mail> isCommand() {
        return predicate(mail -> Attachments.findFirstCommand(mail.getAttachments()).isPresent());
    }

    public static UniPredicate<Mail> isButtonClick() {
        return predicate(mail -> Attachments.findFirstButtonClick(mail.getAttachments()).isPresent());
    }

    public static UniPredicate<Mail> isButtonClickArg(String argType) {
        return predicate(
                mail -> Attachments.findFirstButtonClick(mail.getAttachments())
                        .flatMap(click -> click.getArgByType(argType))
                        .isPresent()
        );
    }

    public static UniPredicate<Mail> isLinks() {
        return predicate(mail -> Attachments.findFirstLink(mail.getAttachments()).isPresent());
    }

}
