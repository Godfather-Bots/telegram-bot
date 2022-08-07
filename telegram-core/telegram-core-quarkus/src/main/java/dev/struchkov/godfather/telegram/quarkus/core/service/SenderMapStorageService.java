package dev.struchkov.godfather.telegram.quarkus.core.service;

import dev.struchkov.godfather.telegram.quarkus.context.service.SenderStorageService;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class SenderMapStorageService implements SenderStorageService {

    private final Map<Long, Integer> lastMessageId = new HashMap<>();

    @Override
    public Uni<Integer> getLastSendMessage(Long telegramId) {
        return Uni.createFrom().item(lastMessageId.get(telegramId));
    }

    @Override
    public Uni<Void> saveLastSendMessage(@NotNull Long telegramId, @NotNull Integer messageId) {
        isNotNull(telegramId);
        lastMessageId.put(telegramId, messageId);
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<Void> removeLastSendMessage(Long telegramId) {
        lastMessageId.remove(telegramId);
        return Uni.createFrom().voidItem();
    }

}
