package dev.struchkov.godfather.telegram.main.context.exception;

public class TelegramSenderException extends TelegramBotException {

    public TelegramSenderException(String message, Throwable cause) {
        super(message, cause);
    }

}
