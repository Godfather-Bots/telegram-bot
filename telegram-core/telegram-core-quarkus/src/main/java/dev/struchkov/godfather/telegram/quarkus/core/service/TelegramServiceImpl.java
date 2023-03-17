package dev.struchkov.godfather.telegram.quarkus.core.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.domain.ClientBotCommand;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramConnect;
import dev.struchkov.godfather.telegram.quarkus.context.service.TelegramService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Checker.checkNull;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class TelegramServiceImpl implements TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramServiceImpl.class);

    private final AbsSender absSender;

    public TelegramServiceImpl(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
    }

    @Override
    public Uni<Void> executeAction(@NotNull String personId, ChatAction chatAction) {
        final SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(personId);
        sendChatAction.setAction(ActionType.valueOf(chatAction.name()));

        return Uni.createFrom().completionStage(getExecuteAsync(sendChatAction))
                .replaceWithVoid();
    }

    @Override
    public Uni<Void> pinMessage(@NotNull String personId, @NotNull String messageId) {
        final PinChatMessage pinChatMessage = new PinChatMessage();
        pinChatMessage.setChatId(personId);
        pinChatMessage.setMessageId(Integer.parseInt(messageId));

        return Uni.createFrom().completionStage(getExecuteAsync(pinChatMessage))
                .replaceWithVoid();
    }

    @Override
    public Uni<Void> unPinMessage(@NotNull String personId, @NotNull String messageId) {
        final UnpinChatMessage unpinChatMessage = new UnpinChatMessage();
        unpinChatMessage.setChatId(personId);
        unpinChatMessage.setMessageId(Integer.parseInt(messageId));

        return Uni.createFrom().completionStage(getExecuteAsync(unpinChatMessage))
                .replaceWithVoid();
    }

    @Override
    public Uni<Void> addCommand(@NotNull Collection<ClientBotCommand> botCommands) {
        return Uni.combine().all()
                .unis(
                        Uni.createFrom().item(
                                botCommands.stream()
                                        .filter(command -> checkNotNull(command.getLang()))
                                        .collect(
                                                Collectors.groupingBy(
                                                        ClientBotCommand::getLang,
                                                        mapping(clientCommand -> BotCommand.builder().command(clientCommand.getKey()).description(clientCommand.getDescription()).build(), toList())
                                                )
                                        )
                        ),
                        Uni.createFrom().item(
                                botCommands.stream()
                                        .filter(command -> checkNull(command.getLang()))
                                        .map(clientCommand -> BotCommand.builder().command(clientCommand.getKey()).description(clientCommand.getDescription()).build())
                                        .toList()
                        )
                ).asTuple()
                .call(t -> {
                    final List<BotCommand> noLangCommands = t.getItem2();
                    if (checkNotEmpty(noLangCommands)) {
                        return Uni.createFrom().completionStage(
                                getExecuteAsync(
                                        SetMyCommands.builder().commands(noLangCommands).build()
                                )
                        );
                    }
                    return Uni.createFrom().voidItem();
                })
                .call(t -> {
                    final Map<String, List<BotCommand>> commandMap = t.getItem1();
                    return Multi.createFrom().iterable(commandMap.entrySet())
                            .call(entry -> Uni.createFrom().completionStage(getExecuteAsync(SetMyCommands.builder().languageCode(entry.getKey()).commands(entry.getValue()).build())))
                            .collect().asList().replaceWithVoid();
                })
                .replaceWithVoid();
    }

    private CompletableFuture<Boolean> getExecuteAsync(SetMyCommands myCommands) {
        try {
            return absSender.executeAsync(myCommands);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Boolean> getExecuteAsync(UnpinChatMessage unpinChatMessage) {
        try {
            return absSender.executeAsync(unpinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletionStage<Boolean> getExecuteAsync(PinChatMessage pinChatMessage) {
        try {
            return absSender.executeAsync(pinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Boolean> getExecuteAsync(SendChatAction sendChatAction) {
        try {
            return absSender.executeAsync(sendChatAction);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

}
