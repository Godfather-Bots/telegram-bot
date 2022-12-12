package dev.struchkov.godfather.telegram.quarkus.core.service;

import dev.struchkov.godfather.telegram.quarkus.context.repository.SenderRepository;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class SenderMapRepository implements SenderRepository {

    private final Map<String, Integer> lastMessageId = new HashMap<>();

    @Override
    public Uni<Integer> getLastSendMessage(String telegramId) {
        return Uni.createFrom().item(lastMessageId.get(telegramId));
    }

    @Override
    public Uni<Void> saveLastSendMessage(@NotNull String telegramId, @NotNull Integer messageId) {
        isNotNull(telegramId);
        lastMessageId.put(telegramId, messageId);
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<Void> removeLastSendMessage(String telegramId) {
        lastMessageId.remove(telegramId);
        return Uni.createFrom().voidItem();
    }

}
