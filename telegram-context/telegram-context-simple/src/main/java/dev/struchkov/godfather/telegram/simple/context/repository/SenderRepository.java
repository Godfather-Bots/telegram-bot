package dev.struchkov.godfather.telegram.simple.context.repository;

import java.util.Optional;

public interface SenderRepository {

    Optional<Integer> getLastSendMessage(String telegramId);

    void saveLastSendMessage(String telegramId, Integer messageId);

    void removeLastSendMessage(String telegramId);

}
