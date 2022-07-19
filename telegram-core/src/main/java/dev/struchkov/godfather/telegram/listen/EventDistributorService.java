package dev.struchkov.godfather.telegram.listen;

import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.service.EventProvider;
import dev.struchkov.godfather.telegram.TelegramConnect;
import dev.struchkov.godfather.telegram.convert.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.convert.MessageMailConvert;
import dev.struchkov.godfather.telegram.convert.SubscribeConvert;
import dev.struchkov.godfather.telegram.convert.UnsubscribeConvert;
import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorService implements EventDistributor {

    private final Map<String, List<EventProvider>> eventProviderMap;

    public EventDistributorService(TelegramConnect telegramConnect, List<EventProvider> eventProviders) {
        this.eventProviderMap = eventProviders.stream().collect(Collectors.groupingBy(EventProvider::getEventType));
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public void processing(@NotNull Update update) {
        if (update.getMessage() != null) {
            final Message message = update.getMessage();
            if (!isEvent(message)) {
                getEventProvider(Mail.TYPE)
                        .ifPresent(eventProviders -> eventProviders.forEach(eventProvider -> eventProvider.sendEvent(MessageMailConvert.apply(message))));
            }
        }
        if (update.getCallbackQuery() != null) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            getEventProvider(Mail.TYPE)
                    .ifPresent(eventProviders -> eventProviders.forEach(eventProvider -> eventProvider.sendEvent(CallbackQueryConvert.apply(callbackQuery))));
        }
        if (update.getMyChatMember() != null) {
            final ChatMemberUpdated chatMember = update.getMyChatMember();
            if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {
                getEventProvider(Unsubscribe.TYPE)
                        .ifPresent(providers -> providers.forEach(provider -> provider.sendEvent(UnsubscribeConvert.apply(chatMember))));
            }
            if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                getEventProvider(Subscribe.TYPE)
                        .ifPresent(eventProviders -> eventProviders.forEach(eventProvider -> eventProvider.sendEvent(SubscribeConvert.apply(chatMember))));
            }
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

    private Optional<List<EventProvider>> getEventProvider(String type) {
        return Optional.ofNullable(eventProviderMap.get(type));
    }

}
