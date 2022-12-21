package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class ButtonClickAttachment extends Attachment {

    /**
     * Идентификатор сообщения, под которым пользователь нажал кнопку.
     */
    private Integer messageId;
    private String rawCallBackData;
    private final Map<String, Arg> args = new HashMap<>();

    public String getRawCallBackData() {
        return rawCallBackData;
    }

    public void setRawCallBackData(String rawCallBackData) {
        this.rawCallBackData = rawCallBackData;
    }

    public void addClickArg(String type, String value) {
        isNotNull(type, value);
        args.put(type, new Arg(type, value));
    }

    public Optional<Arg> getArgByType(String type) {
        isNotNull(type);
        return Optional.ofNullable(args.get(type));
    }

    public Arg getArgByTypeOrThrow(String type) {
        isNotNull(type);
        return Optional.of(args.get(type)).orElseThrow(notFoundException("Аргумент типа {0} не найден.", type));
    }

    public Collection<Arg> getClickArgs() {
        return args.values();
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Map<String, Arg> getArgs() {
        return args;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.BUTTON_CLICK.name();
    }

    public static class Arg {
        private final String type;
        private final String value;

        private Arg(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

    }

}
