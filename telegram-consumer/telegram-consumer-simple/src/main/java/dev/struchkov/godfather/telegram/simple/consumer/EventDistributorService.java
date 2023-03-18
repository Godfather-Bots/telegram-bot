package dev.struchkov.godfather.telegram.simple.consumer;

import dev.struchkov.godfather.main.domain.content.ChatMail;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.context.service.EventHandler;
import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import dev.struchkov.godfather.telegram.main.consumer.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.main.consumer.MessageChatMailConvert;
import dev.struchkov.godfather.telegram.main.consumer.MessageMailConvert;
import dev.struchkov.godfather.telegram.main.consumer.SubscribeConvert;
import dev.struchkov.godfather.telegram.main.consumer.UnsubscribeConvert;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramConnect;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorService implements EventDistributor {

    private final Map<String, List<EventHandler>> eventProviderMap;

    public EventDistributorService(TelegramConnect telegramConnect, List<EventHandler> eventProviders) {
        this.eventProviderMap = eventProviders.stream().collect(Collectors.groupingBy(EventHandler::getEventType));
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public void processing(@NotNull Update update) {
        final Message message = update.getMessage();
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
        final InlineQuery inlineQuery = update.getInlineQuery();

        if (checkNotNull(inlineQuery)) {
            getHandler(inlineQuery.getClass().getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(inlineQuery)));
            return;
        }

        if (checkNotNull(preCheckoutQuery)) {
            getHandler(preCheckoutQuery.getClass().getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(preCheckoutQuery)));
            return;
        }

        if (message != null && (!isEvent(message))) {
            processionMessage(message);
            return;
        }
        if (callbackQuery != null) {
            processionCallback(callbackQuery);
            return;
        }
        if (update.getMyChatMember() != null) {
            final ChatMemberUpdated chatMember = update.getMyChatMember();
            if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {
                getHandler(Unsubscribe.class.getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(UnsubscribeConvert.apply(chatMember))));
                return;
            }
            if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                getHandler(Subscribe.class.getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(SubscribeConvert.apply(chatMember))));
                return;
            }
        }
    }

    private void processionCallback(CallbackQuery callbackQuery) {
        final Long fromId = callbackQuery.getMessage().getChat().getId();
        if (fromId < 0) {

        } else {
            final Mail mail = CallbackQueryConvert.apply(callbackQuery);
            getHandler(Mail.class.getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(mail)));
        }
    }

    private void processionMessage(Message message) {
        final Long fromId = message.getChat().getId();
        if (fromId < 0) {
            final ChatMail chatMail = MessageChatMailConvert.apply(message);
            getHandler(ChatMail.class.getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(chatMail)));
        } else {
            final Mail mail = MessageMailConvert.apply(message);
            getHandler(Mail.class.getSimpleName()).ifPresent(handlers -> handlers.forEach(handler -> handler.handle(mail)));
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

    private Optional<List<EventHandler>> getHandler(String type) {
        return Optional.ofNullable(eventProviderMap.get(type));
    }

}
