package dev.struchkov.godfather.telegram.main.consumer;

import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;

import java.time.LocalDateTime;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class UnsubscribeConvert {

    private UnsubscribeConvert() {
        utilityClass();
    }

    public static Unsubscribe apply(ChatMemberUpdated updated) {
        final Chat chat = updated.getChat();

        final Unsubscribe unsubscribe = new Unsubscribe();
        unsubscribe.setTelegramId(chat.getId());
        unsubscribe.setLastName(chat.getLastName());
        unsubscribe.setFirstName(chat.getFirstName());
        unsubscribe.setSubscriptionDate(LocalDateTime.now());
        return unsubscribe;
    }

}
