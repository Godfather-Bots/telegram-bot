package dev.struchkov.godfather.telegram.main.context.convert;

import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public final class SubscribeConvert {

    private SubscribeConvert() {
        utilityClass();
    }

    public static Subscribe apply(ChatMemberUpdated updated) {
        final User user = updated.getFrom();

        final Subscribe subscribe = new Subscribe();
        subscribe.setTelegramId(user.getId().toString());
        subscribe.setLastName(user.getLastName());
        subscribe.setFirstName(user.getFirstName());
        subscribe.setSubscriptionDate(LocalDateTime.now());
        subscribe.setLogin(user.getUserName());
        return subscribe;
    }

}
