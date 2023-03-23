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
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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

    private final Map<String, List<EventHandler>> eventHandlerMap;

    public EventDistributorService(TelegramConnect telegramConnect, List<EventHandler> eventProviders) {
        this.eventHandlerMap = eventProviders.stream().collect(Collectors.groupingBy(EventHandler::getEventType));
        telegramConnect.initEventDistributor(this);
    }

    @Override
    public Uni<Void> processing(@NotNull Update update) {
        return Uni.createFrom().voidItem()
                .onItem().transformToUni(
                        v -> {
                            final Message message = update.getMessage();
                            final CallbackQuery callbackQuery = update.getCallbackQuery();
                            final PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
                            final InlineQuery inlineQuery = update.getInlineQuery();

                            // запросы к боту из чатов: https://core.telegram.org/bots/inline
                            if (update.hasInlineQuery()) {
                                final Optional<List<EventHandler>> optHandlers = getHandler(inlineQuery.getClass().getSimpleName());
                                if (optHandlers.isPresent()) {
                                    return Multi.createFrom().iterable(optHandlers.get())
                                            .onItem().transformToUni(
                                                    eventHandler -> eventHandler.handle(inlineQuery)
                                            ).concatenate().collect().asList().replaceWithVoid();
                                }
                                return Uni.createFrom().voidItem();
                            }

                            if (update.hasPreCheckoutQuery()) {
                                final Optional<List<EventHandler>> optHandlers = getHandler(preCheckoutQuery.getClass().getName());
                                if (optHandlers.isPresent()) {
                                    return Multi.createFrom().iterable(optHandlers.get())
                                            .onItem().transformToUni(
                                                    eventHandler -> eventHandler.handle(preCheckoutQuery)
                                            ).concatenate().collect().asList().replaceWithVoid();
                                }
                                return Uni.createFrom().voidItem();
                            }

                            if (update.hasMessage()) {
                                final Optional<List<EventHandler>> optHandlers = getHandler(Mail.class.getName());
                                if (optHandlers.isPresent()) {
                                    return Multi.createFrom().iterable(optHandlers.get())
                                            .onItem().transformToUni(
                                                    eventHandler -> eventHandler.handle(MessageMailConvert.apply(message))
                                            ).concatenate().collect().asList().replaceWithVoid();
                                }
                                return Uni.createFrom().voidItem();
                            }

                            if (update.hasCallbackQuery()) {
                                final Optional<List<EventHandler>> optHandlers = getHandler(Mail.class.getName());
                                if (optHandlers.isPresent()) {
                                    return Multi.createFrom().iterable(optHandlers.get())
                                            .onItem().transformToUni(
                                                    eventHandler -> eventHandler.handle(CallbackQueryConvert.apply(callbackQuery))
                                            ).concatenate().collect().asList().replaceWithVoid();
                                }
                                return Uni.createFrom().voidItem();
                            }

                            if (update.hasMyChatMember()) {
                                final ChatMemberUpdated chatMember = update.getMyChatMember();
                                if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {

                                    final Optional<List<EventHandler>> optHandlers = getHandler(Unsubscribe.class.getName());
                                    if (optHandlers.isPresent()) {
                                        return Multi.createFrom().iterable(optHandlers.get())
                                                .onItem().transformToUni(
                                                        eventHandler -> eventHandler.handle(UnsubscribeConvert.apply(chatMember))
                                                ).concatenate().collect().asList().replaceWithVoid();
                                    }
                                    return Uni.createFrom().voidItem();
                                }
                                if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                                    final Optional<List<EventHandler>> optHandlers = getHandler(Subscribe.class.getName());
                                    if (optHandlers.isPresent()) {
                                        return Multi.createFrom().iterable(optHandlers.get())
                                                .onItem().transformToUni(
                                                        eventHandler -> eventHandler.handle(SubscribeConvert.apply(chatMember))
                                                ).concatenate().collect().asList().replaceWithVoid();
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
