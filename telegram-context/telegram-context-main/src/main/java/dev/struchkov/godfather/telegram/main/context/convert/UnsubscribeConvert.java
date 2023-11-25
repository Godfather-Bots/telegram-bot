package dev.struchkov.godfather.telegram.main.context.convert;

import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class UnsubscribeConvert {

    private UnsubscribeConvert() {
        utilityClass();
    }

    public static Unsubscribe apply(ChatMemberUpdated updated) {
        final User user = updated.getFrom();

        final Unsubscribe unsubscribe = new Unsubscribe();
        unsubscribe.setTelegramId(user.getId().toString());
        unsubscribe.setLogin(user.getUserName());
        unsubscribe.setFirstName(user.getFirstName());
        unsubscribe.setUnsubscriptionDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(updated.getDate()), ZoneId.systemDefault()));
        unsubscribe.setPremium(convert(user.getIsPremium()));
        unsubscribe.setLastName(user.getLastName());
        unsubscribe.setLanguageCode(user.getLanguageCode());
        return unsubscribe;
    }

    private static boolean convert(Boolean isPremium) {
        if (checkNotNull(isPremium)) {
            return isPremium;
        }
        return false;
    }

}
