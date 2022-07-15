package dev.struchkov.godfather.telegram.convert;

import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;

import java.time.LocalDateTime;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class SubscribeConvert {

    private SubscribeConvert() {
        utilityClass();
    }

    public static Subscribe apply(ChatMemberUpdated updated) {
        final Chat chat = updated.getChat();

        final Subscribe subscribe = new Subscribe();
        subscribe.setTelegramId(chat.getId());
        subscribe.setLastName(chat.getLastName());
        subscribe.setFirstName(chat.getFirstName());
        subscribe.setSubscriptionDate(LocalDateTime.now());
        return subscribe;
    }

}
