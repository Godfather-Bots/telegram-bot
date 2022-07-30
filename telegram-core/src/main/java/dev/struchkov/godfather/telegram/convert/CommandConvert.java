package dev.struchkov.godfather.telegram.convert;

import dev.struchkov.godfather.telegram.domain.event.Command;
import dev.struchkov.haiti.utils.Strings;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

import static dev.struchkov.haiti.context.exception.ConvertException.convertException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;
import static dev.struchkov.haiti.utils.Inspector.isNotEmpty;
import static dev.struchkov.haiti.utils.Inspector.isTrue;

public class CommandConvert {

    private CommandConvert() {
        utilityClass();
    }

    public static Command apply(Message message) {
        final List<MessageEntity> entities = message.getEntities();
        isNotEmpty(entities, convertException("Ошибка преобразования сообщения в команду. В сообщении не обнаружена команда."));

        final MessageEntity messageEntity = entities.get(0);
        isTrue("bot_command".equals(messageEntity.getType()), convertException("Ошибка преобразования сообщения в команду. В сообщении не обнаружена команда."));

        final String commandValue = messageEntity.getText();
        String commandArg = message.getText().replace(commandValue, "");
        if (checkNotEmpty(commandArg)) {
            commandArg = commandArg.substring(1);
        }

        final Command command = new Command();
        command.setValue(commandValue);
        command.setCommandType(commandValue.replace("/", ""));
        command.setArg(Strings.EMPTY.equals(commandArg) ? null : commandArg);
        command.setRawValue(message.getText());

        final Chat chat = message.getChat();

        command.setFirstName(chat.getFirstName());
        command.setLastName(chat.getLastName());
        command.setPersonId(chat.getId());
        return command;
    }

}
