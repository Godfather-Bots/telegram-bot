package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;

import java.util.Optional;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

public class CommandAttachment extends Attachment {

    private String value;
    private String commandType;
    private String arg;
    private String rawValue;

    public void setValue(String value) {
        this.value = value;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getValue() {
        return value;
    }

    public Optional<String> getArg() {
        return Optional.ofNullable(arg);
    }

    public String getCommandType() {
        return commandType;
    }

    public boolean isCommandType(String type) {
        if (checkNotNull(type)) {
            return type.equals(commandType);
        }
        return false;
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.COMMAND.name();
    }
}
