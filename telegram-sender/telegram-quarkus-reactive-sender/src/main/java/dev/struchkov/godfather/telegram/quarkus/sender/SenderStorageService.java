package dev.struchkov.godfather.telegram.quarkus.sender;

import io.smallrye.mutiny.Uni;

public interface SenderStorageService {

    Uni<Integer> getLastSendMessage(Long telegramId);

    Uni<Void> saveLastSendMessage(Long telegramId, Integer messageId);

    Uni<Void> removeLastSendMessage(Long telegramId);

}
