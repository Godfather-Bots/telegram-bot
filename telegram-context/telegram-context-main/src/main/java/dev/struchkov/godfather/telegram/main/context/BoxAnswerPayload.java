package dev.struchkov.godfather.telegram.main.context;

import dev.struchkov.haiti.utils.Exceptions;
import dev.struchkov.haiti.utils.container.ContextKey;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;

public final class BoxAnswerPayload {

    public static final ContextKey<Boolean> DISABLE_WEB_PAGE_PREVIEW = ContextKey.of("DISABLE_WEB_PAGE_PREVIEW", Boolean.class);
    public static final ContextKey<Boolean> DISABLE_NOTIFICATION = ContextKey.of("DISABLE_NOTIFICATION", Boolean.class);
    public static final ContextKey<SendInvoice> INVOICE = ContextKey.of("INVOICE", SendInvoice.class);

    private BoxAnswerPayload() {
        Exceptions.utilityClass();
    }

}
