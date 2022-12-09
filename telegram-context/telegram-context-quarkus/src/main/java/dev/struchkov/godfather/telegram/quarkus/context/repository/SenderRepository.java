package dev.struchkov.godfather.telegram.quarkus.context.repository;

import io.smallrye.mutiny.Uni;

public interface SenderRepository {

    Uni<Integer> getLastSendMessage(String telegramId);

    Uni<Void> saveLastSendMessage(String telegramId, Integer messageId);

    Uni<Void> removeLastSendMessage(String telegramId);

}
