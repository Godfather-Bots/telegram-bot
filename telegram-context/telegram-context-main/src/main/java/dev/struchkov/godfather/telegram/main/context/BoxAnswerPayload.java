package dev.struchkov.godfather.telegram.main.context;

import dev.struchkov.haiti.utils.Exceptions;
import dev.struchkov.haiti.utils.container.ContextKey;

public final class BoxAnswerPayload {

    public static final ContextKey<Boolean> DISABLE_WEB_PAGE_PREVIEW = ContextKey.of("DISABLE_WEB_PAGE_PREVIEW", Boolean.class);
    public static final ContextKey<Boolean> DISABLE_NOTIFICATION = ContextKey.of("DISABLE_NOTIFICATION", Boolean.class);

    private BoxAnswerPayload() {
        Exceptions.utilityClass();
    }

}
