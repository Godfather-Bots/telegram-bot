package dev.struchkov.godfather.telegram.main.context.exception;

public class TelegramBanBotException extends TelegramSenderException{

    public TelegramBanBotException(String message, Throwable cause) {
        super(message, cause);
    }

}
