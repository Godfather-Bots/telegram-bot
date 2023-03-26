package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Map<String, Arg> args = new HashMap<>();

    public ButtonClickAttachment() {
        super(TelegramAttachmentType.BUTTON_CLICK.name());
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arg {

        private String type;
        private String value;

    }

}
