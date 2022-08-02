package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.context.domain.content.attachment.Attachment;

import java.util.Optional;

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

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.COMMAND.name();
    }
}
