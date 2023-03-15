package dev.struchkov.godfather.telegram.simple.core.service;

import dev.struchkov.godfather.telegram.domain.ChatAction;
import dev.struchkov.godfather.telegram.domain.ClientBotCommand;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramService;
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
    public void executeAction(@NotNull String personId, ChatAction chatAction) {
        final SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(personId);
        sendChatAction.setAction(ActionType.valueOf(chatAction.name()));

        try {
            absSender.execute(sendChatAction);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void pinMessage(@NotNull String personId, @NotNull String messageId) {
        final PinChatMessage pinChatMessage = new PinChatMessage();
        pinChatMessage.setChatId(personId);
        pinChatMessage.setMessageId(Integer.parseInt(messageId));
        try {
            absSender.execute(pinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void unPinMessage(@NotNull String personId, @NotNull String messageId) {
        final UnpinChatMessage unpinChatMessage = new UnpinChatMessage();
        unpinChatMessage.setChatId(personId);
        unpinChatMessage.setMessageId(Integer.parseInt(messageId));
        try {
            absSender.execute(unpinChatMessage);
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void addCommand(@NotNull Collection<ClientBotCommand> botCommands) {
        final Map<String, List<BotCommand>> commandMap = botCommands.stream()
                .filter(command -> checkNotNull(command.getLang()))
                .collect(
                        Collectors.groupingBy(
                                ClientBotCommand::getLang,
                                mapping(clientCommand -> BotCommand.builder().command(clientCommand.getKey()).description(clientCommand.getDescription()).build(), toList())
                        )
                );

        final List<@NotNull BotCommand> noLangCommands = botCommands.stream()
                .filter(command -> checkNull(command.getLang()))
                .map(clientCommand -> BotCommand.builder()
                        .command(clientCommand.getKey())
                        .description(clientCommand.getDescription())
                        .build())
                .toList();

        try {
            if (checkNotEmpty(noLangCommands)) {
                absSender.execute(SetMyCommands.builder().commands(noLangCommands).build());
            }

            for (Map.Entry<String, List<BotCommand>> entry : commandMap.entrySet()) {
                absSender.execute(SetMyCommands.builder().languageCode(entry.getKey()).commands(entry.getValue()).build());
            }
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
