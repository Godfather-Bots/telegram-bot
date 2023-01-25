package dev.struchkov.godfather.telegram.quarkus.context.repository;

import io.smallrye.mutiny.Uni;

public interface SenderRepository {

    Uni<String> getLastSendMessage(String telegramId);

    Uni<Void> saveLastSendMessage(String telegramId, String messageId);

    Uni<Void> removeLastSendMessage(String telegramId);

}
