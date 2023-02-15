package dev.struchkov.godfather.telegram.main.context;

import dev.struchkov.godfather.main.domain.ContextKey;
import dev.struchkov.haiti.utils.Exceptions;

public final class BoxAnswerPayload {

    public static final ContextKey<Boolean> DISABLE_WEB_PAGE_PREVIEW = ContextKey.of("DISABLE_WEB_PAGE_PREVIEW", Boolean.class);
    public static final ContextKey<Boolean> DISABLE_NOTIFICATION = ContextKey.of("DISABLE_NOTIFICATION", Boolean.class);

    private BoxAnswerPayload() {
        Exceptions.utilityClass();
    }

}
