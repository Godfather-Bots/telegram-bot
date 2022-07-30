package dev.struchkov.godfather.telegram.context;

import java.util.Optional;

public interface SenderStorageService {

    Optional<Integer> getLastSendMessage(Long telegramId);

    void saveLastSendMessage(Long telegramId, Integer messageId);

    void removeLastSendMessage(Long telegramId);

}
