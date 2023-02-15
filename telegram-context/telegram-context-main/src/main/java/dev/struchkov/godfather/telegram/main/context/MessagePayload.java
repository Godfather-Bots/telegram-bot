package dev.struchkov.godfather.telegram.main.context;

import dev.struchkov.godfather.main.domain.ContextKey;
import dev.struchkov.haiti.utils.Exceptions;

public final class MessagePayload {

    public static final ContextKey<String> USERNAME = ContextKey.of("KEY", String.class);

    private MessagePayload() {
        Exceptions.utilityClass();
    }

}
