package dev.struchkov.godfather.telegram.quarkus.consumer;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.quarkus.context.service.EventHandler;
import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import dev.struchkov.godfather.telegram.main.consumer.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.main.consumer.MessageMailConvert;
import dev.struchkov.godfather.telegram.main.consumer.SubscribeConvert;
import dev.struchkov.godfather.telegram.main.consumer.UnsubscribeConvert;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.quarkus.core.TelegramConnectBot;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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

import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
public class EventDistributorService implements EventDistributor {

    private final Map<String, List<EventHandler>> eventHandlerMap;

    public EventDistributorService(TelegramConnectBot telegramConnectBot, List<EventHandler> eventProviders) {
        this.eventHandlerMap = eventProviders.stream().collect(Collectors.groupingBy(EventHandler::getEventType));
        telegramConnectBot.initEventDistributor(this);
    }

    @Override
    public Uni<Void> processing(@NotNull Update update) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final Message message = update.getMessage();
                            final CallbackQuery callbackQuery = update.getCallbackQuery();
                            if (checkNotNull(message)) {
                                if (!isEvent(message)) {
                                    final Optional<List<EventHandler>> optHandlers = getHandler(Mail.TYPE);
                                    if (optHandlers.isPresent()) {
                                        return Multi.createFrom().iterable(optHandlers.get())
                                                .onItem().transformToUni(
                                                        eventHandler -> eventHandler.handle(MessageMailConvert.apply(message))
                                                ).concatenate().toUni().replaceWithVoid();
                                    }
                                    return Uni.createFrom().voidItem();
                                }
                            }
                            if (checkNotNull(callbackQuery)) {
                                final Optional<List<EventHandler>> optHandlers = getHandler(Mail.TYPE);
                                if (optHandlers.isPresent()) {
                                    return Multi.createFrom().iterable(optHandlers.get())
                                            .onItem().transformToUni(
                                                    eventHandler -> eventHandler.handle(CallbackQueryConvert.apply(callbackQuery))
                                            ).concatenate().toUni().replaceWithVoid();
                                }
                                return Uni.createFrom().voidItem();
                            }
                            if (checkNotNull(update.getMyChatMember())) {
                                final ChatMemberUpdated chatMember = update.getMyChatMember();
                                if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {

                                    final Optional<List<EventHandler>> optHandlers = getHandler(Unsubscribe.TYPE);
                                    if (optHandlers.isPresent()) {
                                        return Multi.createFrom().iterable(optHandlers.get())
                                                .onItem().transformToUni(
                                                        eventHandler -> eventHandler.handle(UnsubscribeConvert.apply(chatMember))
                                                ).concatenate().toUni().replaceWithVoid();
                                    }
                                    return Uni.createFrom().voidItem();
                                }
                                if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                                    final Optional<List<EventHandler>> optHandlers = getHandler(Subscribe.TYPE);
                                    if (optHandlers.isPresent()) {
                                        return Multi.createFrom().iterable(optHandlers.get())
                                                .onItem().transformToUni(
                                                        eventHandler -> eventHandler.handle(SubscribeConvert.apply(chatMember))
                                                ).concatenate().toUni().replaceWithVoid();
                                    }
                                    return Uni.createFrom().voidItem();
                                }
                            }
                            return Uni.createFrom().voidItem();
                        }
                );
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
        return Optional.ofNullable(eventHandlerMap.get(type));
    }

}
