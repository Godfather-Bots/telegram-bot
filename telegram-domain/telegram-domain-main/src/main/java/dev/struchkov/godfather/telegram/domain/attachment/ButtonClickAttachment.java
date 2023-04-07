package dev.struchkov.godfather.telegram.domain.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

@Getter
@Setter
public class ButtonClickAttachment extends Attachment {

    /**
     * Идентификатор сообщения, под которым пользователь нажал кнопку.
     */
    private String messageId;
    private String rawCallBackData;

    private Map<String, ButtonArg> args = new HashMap<>();

    public ButtonClickAttachment() {
        super(TelegramAttachmentType.BUTTON_CLICK.name());
    }

    @JsonIgnore
    public void addClickArg(String type, String value) {
        isNotNull(type, value);
        args.put(type, ButtonArg.buttonArg(type, value));
    }

    @JsonIgnore
    public Optional<ButtonArg> getArgByType(String type) {
        isNotNull(type);
        return Optional.ofNullable(args.get(type));
    }

    @JsonIgnore
    public Collection<ButtonArg> getClickArgs() {
        return args.values();
    }

    @JsonIgnore
    public ButtonArg getArgByTypeOrThrow(String type) {
        isNotNull(type);
        return Optional.of(args.get(type)).orElseThrow(notFoundException("Аргумент типа {0} не найден.", type));
    }

}
