package dev.struchkov.godfather.telegram.webhook;

import dev.struchkov.godfather.telegram.domain.config.TelegramBotConfig;
import dev.struchkov.godfather.telegram.quarkus.context.service.EventDistributor;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dev.struchkov.haiti.context.exception.AccessException.accessException;
import static dev.struchkov.haiti.utils.Inspector.isTrue;

@Slf4j
@Path("callback")
public class WebhookController {

    private static final String ERROR_ACCESS = "В доступе отказано!";
    private static final String PATH_KEY = "bot";
    private final String secretToken;
    private final EventDistributor eventDistributor;

    public WebhookController(TelegramBotConfig telegramBotConfig, EventDistributor eventDistributor) {
        this.secretToken = telegramBotConfig.getWebhookConfig().getSecretToken();
        this.eventDistributor = eventDistributor;
    }

    @POST
    @Path("{webhookPath}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateReceived(@PathParam("webhookPath") String botPath, @HeaderParam("X-Telegram-Bot-Api-Secret-Token") String secretTokenFromTelegram, Update update) {
        Uni.createFrom().voidItem()
                .invoke(() -> {
                    log.trace("Получено webhook событие: {}", update);
                    isTrue(PATH_KEY.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(secretToken.equals(secretTokenFromTelegram), accessException(ERROR_ACCESS));
                })
                .call(() -> eventDistributor.processing(update))
                .subscribe().with(
                        v -> log.trace("Webhook событие обработано. Идентификатор: {}", update.getUpdateId()),
                        fail -> log.error("При обработке webhook события произошла ошибка. Идентификатор события: " + update.getUpdateId(), fail)
                );

        return Uni.createFrom().item(Response.ok().build())
                .invoke(() -> log.trace("Сообщили Telegram, что вебхук событие принято. Идентификатор события: {}", update.getUpdateId()));
    }

    @GET
    @Path("{webhookPath}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> testReceived(@PathParam("webhookPath") String botPath, @HeaderParam("X-Telegram-Bot-Api-Secret-Token") String secretTokenFromTelegram) {
        return Uni.createFrom().voidItem()
                .onItem().invoke(() -> {
                    isTrue(PATH_KEY.equals(botPath), accessException(ERROR_ACCESS));
                    isTrue(secretToken.equals(secretTokenFromTelegram), accessException(ERROR_ACCESS));
                })
                .onItem().transform(ignore -> "Hi there " + botPath + "!");
    }

}
