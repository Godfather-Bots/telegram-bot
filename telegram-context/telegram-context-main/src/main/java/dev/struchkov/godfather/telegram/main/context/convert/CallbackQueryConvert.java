package dev.struchkov.godfather.telegram.main.context.convert;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
public class CallbackQueryConvert {

    public static Mail apply(CallbackQuery callbackQuery) {
        final String callbackData = callbackQuery.getData();

        final Mail mail = new Mail();
        mail.setId(callbackQuery.getMessage().getMessageId().toString());
        mail.setCreateDate(LocalDateTime.now());
        mail.setText(callbackData);
        mail.addAttachment(convertToButtonClick(callbackData, callbackQuery.getMessage().getMessageId()));

        final Long chatId = callbackQuery.getMessage().getChatId();
        mail.setFromPersonId(chatId != null ? chatId.toString() : null);

        final User user = callbackQuery.getFrom();
        mail.setFirstName(user.getFirstName());
        mail.setLastName(user.getLastName());
        return mail;
    }

    private static ButtonClickAttachment convertToButtonClick(String callbackData, Integer messageId) {
        final ButtonClickAttachment buttonClickAttachment = new ButtonClickAttachment();
        buttonClickAttachment.setRawCallBackData(callbackData);
        buttonClickAttachment.setMessageId(messageId.toString());
        if (callbackData.charAt(0) == '[' && callbackData.charAt(callbackData.length() - 1) == ']') {
            final String[] args = callbackData.substring(1, callbackData.length() - 1).split(";");
            for (String arg : args) {
                final String[] oneArg = arg.split(":");
                final String key = oneArg[0];
                final String value = oneArg[1];
                buttonClickAttachment.addClickArg(key, value);
            }
        }
        return buttonClickAttachment;
    }

}
