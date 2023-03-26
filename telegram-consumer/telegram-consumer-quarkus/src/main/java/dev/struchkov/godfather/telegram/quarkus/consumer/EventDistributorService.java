package dev.struchkov.godfather.telegram.quarkus.consumer;

import dev.struchkov.godfather.main.domain.EventContainer;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.quarkus.context.service.EventDispatching;
import dev.struchkov.godfather.telegram.domain.event.Subscribe;
import dev.struchkov.godfather.telegram.domain.event.Unsubscribe;
import dev.struchkov.godfather.telegram.main.consumer.CallbackQueryConvert;
import dev.struchkov.godfather.telegram.main.consumer.MessageMailConvert;
import dev.struchkov.godfather.telegram.main.consumer.SubscribeConvert;
import dev.struchkov.godfather.telegram.main.consumer.UnsubscribeConvert;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
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
                                return Uni.createFrom().item(new EventContainer<>(InlineQuery.class, inlineQuery));
                            }

                            if (update.hasPreCheckoutQuery()) {
                                return Uni.createFrom().item(new EventContainer<>(PreCheckoutQuery.class, preCheckoutQuery));
                            }

                            if (update.hasMessage()) {
                                return Uni.createFrom().item(new EventContainer<>(Mail.class, MessageMailConvert.apply(message)));
                            }

                            if (update.hasCallbackQuery()) {
                                return Uni.createFrom().item(new EventContainer<>(Mail.class, CallbackQueryConvert.apply(callbackQuery)));
                            }

                            if (update.hasMyChatMember()) {
                                final ChatMemberUpdated chatMember = update.getMyChatMember();
                                if ("kicked".equals(chatMember.getNewChatMember().getStatus())) {
                                    return Uni.createFrom().item(new EventContainer<>(Unsubscribe.class, UnsubscribeConvert.apply(chatMember)));
                                }
                                if ("member".equals(chatMember.getNewChatMember().getStatus())) {
                                    return Uni.createFrom().item(new EventContainer<>(Subscribe.class, SubscribeConvert.apply(chatMember)));
                                }
                            }
                            return Uni.createFrom().nullItem();
                        }
                ).onItem().ifNotNull().transformToUni(eventDispatching::dispatch);
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
