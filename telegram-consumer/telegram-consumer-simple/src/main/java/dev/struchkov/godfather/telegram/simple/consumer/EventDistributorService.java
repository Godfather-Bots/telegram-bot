package dev.struchkov.godfather.telegram.simple.consumer;

import dev.struchkov.godfather.main.domain.EventContainer;
import dev.struchkov.godfather.main.domain.content.ChatMail;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.context.service.EventDispatching;
import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import dev.struchkov.godfather.telegram.main.context.convert.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.main.context.convert.MessageChatMailConvert;
import dev.struchkov.godfather.telegram.main.context.convert.MessageMailConvert;
import dev.struchkov.godfather.telegram.main.context.convert.SubscribeConvert;
import dev.struchkov.godfather.telegram.main.context.convert.UnsubscribeConvert;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

import java.util.List;

import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorService implements EventDistributor {

    private final EventDispatching eventDispatching;

    public EventDistributorService(TelegramConnect telegramConnect, EventDispatching eventDispatching) {
        this.eventDispatching = eventDispatching;
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public void processing(@NotNull Update update) {
        final Message message = update.getMessage();
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
        final InlineQuery inlineQuery = update.getInlineQuery();

        if (update.hasInlineQuery()) {
            eventDispatching.dispatch(new EventContainer<>(InlineQuery.class, inlineQuery));
            return;
        }

        if (update.hasChosenInlineQuery()) {
            eventDispatching.dispatch(new EventContainer<>(ChosenInlineQuery.class, update.getChosenInlineQuery()));
        }

        if (update.hasPreCheckoutQuery()) {
            eventDispatching.dispatch(new EventContainer<>(PreCheckoutQuery.class, preCheckoutQuery));
            return;
        }

        if (update.hasMessage()) {
            processionMessage(message);
            return;
        }
        if (update.hasCallbackQuery()) {
            processionCallback(callbackQuery);
            return;
        }
        if (update.hasMyChatMember()) {
            final ChatMemberUpdated chatMember = update.getMyChatMember();
            if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {
                final Unsubscribe unsubscribe = UnsubscribeConvert.apply(chatMember);
                eventDispatching.dispatch(new EventContainer<>(Unsubscribe.class, unsubscribe));
                return;
            }
            if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                final Subscribe subscribe = SubscribeConvert.apply(chatMember);
                eventDispatching.dispatch(new EventContainer<>(Subscribe.class, subscribe));
                return;
            }
        }
    }

    private void processionCallback(CallbackQuery callbackQuery) {
        final Message message = callbackQuery.getMessage();
        if (checkNotBlank(callbackQuery.getInlineMessageId())) {
            return;
        }
        if (checkNotNull(message)) {
            final Long fromId = message.getChat().getId();
            if (fromId < 0) {

            } else {
                final Mail mail = CallbackQueryConvert.apply(callbackQuery);
                eventDispatching.dispatch(new EventContainer<>(Mail.class, mail));
            }
        }
    }

    private void processionMessage(Message message) {
        final Long fromId = message.getChat().getId();
        if (fromId < 0) {
            final ChatMail chatMail = MessageChatMailConvert.apply(message);
            eventDispatching.dispatch(new EventContainer<>(ChatMail.class, chatMail));
        } else {
            final Mail mail = MessageMailConvert.apply(message);
            eventDispatching.dispatch(new EventContainer<>(Mail.class, mail));
        }
    }

    private boolean isEvent(Message message) {
        return message.getChannelChatCreated() != null
               || message.getDeleteChatPhoto() != null
               || isNewChatMember(message.getNewChatMembers())
               || message.getNewChatTitle() != null
               || message.getNewChatPhoto() != null
               || message.getVideoChatEnded() != null
               || message.getVideoChatParticipantsInvited() != null
               || message.getVideoChatScheduled() != null
               || message.getVideoNote() != null
               || message.getVideoChatStarted() != null;
    }

    private boolean isNewChatMember(List<User> newChatMembers) {
        if (newChatMembers == null) {
            return true;
        } else {
            return !newChatMembers.isEmpty();
        }
    }

}
