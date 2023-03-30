package dev.struchkov.godfather.telegram.main.context.convert;

import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class UnsubscribeConvert {

    private UnsubscribeConvert() {
        utilityClass();
    }

    public static Unsubscribe apply(ChatMemberUpdated updated) {
        final User user = updated.getFrom();

        final Unsubscribe unsubscribe = new Unsubscribe();
        unsubscribe.setTelegramId(user.getId().toString());
        unsubscribe.setLastName(user.getLastName());
        unsubscribe.setFirstName(user.getFirstName());
        unsubscribe.setUnsubscriptionDate(LocalDateTime.now());
        return unsubscribe;
    }

}
