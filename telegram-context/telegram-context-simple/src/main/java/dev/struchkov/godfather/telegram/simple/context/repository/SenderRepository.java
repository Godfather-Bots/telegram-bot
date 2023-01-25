package dev.struchkov.godfather.telegram.simple.context.repository;

import java.util.Optional;

public interface SenderRepository {

    Optional<String> getLastSendMessage(String telegramId);

    void saveLastSendMessage(String telegramId, String messageId);

    void removeLastSendMessage(String telegramId);

}
