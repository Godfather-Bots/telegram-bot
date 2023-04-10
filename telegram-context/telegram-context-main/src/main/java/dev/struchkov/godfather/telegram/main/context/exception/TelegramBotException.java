package dev.struchkov.godfather.telegram.main.context.exception;

public class TelegramBotException extends RuntimeException {

    public TelegramBotException() {
    }

    public TelegramBotException(String message) {
        super(message);
    }

    public TelegramBotException(String message, Throwable cause) {
        super(message, cause);
    }

}
