package dev.struchkov.godfather.telegram.simple.context.service;

import java.util.Optional;

public interface SenderStorageService {

    Optional<Integer> getLastSendMessage(String telegramId);

    void saveLastSendMessage(String telegramId, Integer messageId);

    void removeLastSendMessage(String telegramId);

}
