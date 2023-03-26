package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

@Getter
@Setter
public class CommandAttachment extends Attachment {

    private String value;
    private String commandType;
    private String arg;
    private String rawValue;

    public CommandAttachment() {
        super(TelegramAttachmentType.COMMAND.name());
    }

    public Optional<String> getArg() {
        return Optional.ofNullable(arg);
    }

    public boolean isCommandType(String type) {
        if (checkNotNull(type)) {
            return type.equals(commandType);
        }
        return false;
    }

}
