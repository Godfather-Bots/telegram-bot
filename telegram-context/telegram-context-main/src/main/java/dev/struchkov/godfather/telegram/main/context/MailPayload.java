package dev.struchkov.godfather.telegram.main.context;

import dev.struchkov.haiti.utils.Exceptions;
import dev.struchkov.haiti.utils.container.ContextKey;

public final class MailPayload {

    public static final ContextKey<String> USERNAME = ContextKey.of("KEY", String.class);

    private MailPayload() {
        Exceptions.utilityClass();
    }

}
