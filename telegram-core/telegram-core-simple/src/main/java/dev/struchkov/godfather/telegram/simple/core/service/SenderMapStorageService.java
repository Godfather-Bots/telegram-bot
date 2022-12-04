package dev.struchkov.godfather.telegram.simple.core.service;

import dev.struchkov.godfather.telegram.simple.context.service.SenderStorageService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class SenderMapStorageService implements SenderStorageService {

    private final Map<String, Integer> lastMessageId = new HashMap<>();

    @Override
    public Optional<Integer> getLastSendMessage(String telegramId) {
        return Optional.ofNullable(lastMessageId.get(telegramId));
    }

    @Override
    public void saveLastSendMessage(@NotNull String telegramId, @NotNull Integer messageId) {
        isNotNull(telegramId);
        lastMessageId.put(telegramId, messageId);
    }

    @Override
    public void removeLastSendMessage(String telegramId) {
        lastMessageId.remove(telegramId);
    }

}
