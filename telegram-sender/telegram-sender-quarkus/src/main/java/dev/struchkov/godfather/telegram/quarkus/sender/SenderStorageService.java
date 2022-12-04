package dev.struchkov.godfather.telegram.quarkus.sender;

import io.smallrye.mutiny.Uni;

public interface SenderStorageService {

    Uni<Integer> getLastSendMessage(String telegramId);

    Uni<Void> saveLastSendMessage(String telegramId, Integer messageId);

    Uni<Void> removeLastSendMessage(String telegramId);

}
